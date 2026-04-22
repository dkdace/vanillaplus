package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.ReloadableDataManager;
import com.dace.vanillaplus.data.RaidWave;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPGameRules;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(Raid.class)
public abstract class RaidMixin implements VPMixin<Raid> {
    @Unique
    private static final BiFunction<Object, Object, MutableComponent> COMPONENT_RAID_WAVES = (arg1, arg2) ->
            Component.translatable("event.minecraft.raid.waves", arg1, arg2);
    @Unique
    private static final Function<Object, Component> COMPONENT_RAID_TIME_REMAINING = arg ->
            Component.translatable("event.minecraft.raid.time_remaining", arg);
    @Shadow
    @Final
    private static final int RAID_TIMEOUT_TICKS = 3 * 60 * 20;

    @Shadow
    @Final
    private ServerBossEvent raidEvent;
    @Shadow
    @Final
    private int numGroups;
    @Shadow
    private int groupsSpawned;
    @Shadow
    private float totalHealth;
    @Shadow
    private Optional<BlockPos> waveSpawnPos;
    @Shadow
    private long ticksActive;

    @Shadow
    protected abstract void setDirty(ServerLevel serverLevel);

    @Shadow
    public abstract void setLeader(int wave, Raider raider);

    @Shadow
    public abstract void joinRaid(ServerLevel serverLevel, int wave, Raider raider, @Nullable BlockPos blockPos, boolean isRecruited);

    @Shadow
    protected abstract boolean isFinalWave();

    @Unique
    private void spawnRaiders(@NonNull ServerLevel serverLevel, @NonNull BlockPos blockPos, @NonNull RaidWave raidWave, int wave) {
        boolean hasLeader = false;

        for (Map.Entry<RaidWave.RaiderType, Integer> entry : raidWave.getRaiderCountMap(wave).entrySet()) {
            for (int j = 0; j < entry.getValue(); j++) {
                RaidWave.RaiderType raiderType = entry.getKey();
                Raider raider = raiderType.getEntityType().create(serverLevel, EntitySpawnReason.EVENT);

                if (raider == null)
                    break;

                joinRaid(serverLevel, wave, raider, blockPos, false);

                if (!hasLeader && raider.canBeLeader()) {
                    raider.setPatrolLeader(true);
                    setLeader(wave, raider);
                    hasLeader = true;
                }

                spawnRidingRaider(serverLevel, blockPos, wave, raiderType, raider);
            }
        }
    }

    @Unique
    private void spawnRidingRaider(@NonNull ServerLevel serverLevel, @NonNull BlockPos blockPos, int wave, @NonNull RaidWave.RaiderType raiderType,
                                   @NonNull Raider raider) {
        EntityType<? extends Raider> ridingEntityType = raiderType.getRidingEntityType();
        if (ridingEntityType == null)
            return;

        Raider ridingRaider = ridingEntityType.create(serverLevel, EntitySpawnReason.EVENT);
        if (ridingRaider == null)
            return;

        joinRaid(serverLevel, wave, ridingRaider, blockPos, false);

        ridingRaider.snapTo(blockPos, 0, 0);
        ridingRaider.startRiding(raider);
    }

    @ModifyArg(method = "absorbRaidOmen", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"), index = 2)
    private int modifyMaxRaidOmenLevel(int max, @Local(argsOnly = true) ServerPlayer serverPlayer) {
        return VPGameRules.getValue(VPGameRules.MAX_BAD_OMEN_LEVEL, serverPlayer.level());
    }

    @Overwrite
    private boolean hasBonusWave() {
        return false;
    }

    @Overwrite
    public int getNumGroups(Difficulty difficulty) {
        return ReloadableDataManager.RAID_WAVE.get(difficulty).map(RaidWave::getTotalWaves).orElse(0);
    }

    @Definition(id = "i", local = @Local(type = int.class, ordinal = 0))
    @Expression("i <= 2")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyRaidBarRaidersRemainingCondition(boolean original) {
        return ticksActive <= 20;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/raid/Raid;RAID_NAME_COMPONENT:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETSTATIC))
    private Component modifyRaidBarName(Component originalComponent, @Local int raiderCount) {
        int wave = groupsSpawned;
        if (raiderCount == 0 && !isFinalWave())
            wave++;

        return COMPONENT_RAID_WAVES.apply(wave, numGroups);
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerBossEvent;setName(Lnet/minecraft/network/chat/Component;)V", ordinal = 2))
    private Component addRaidBarTimer(Component originalComponent, @Local(argsOnly = true) ServerLevel serverLevel) {
        Component timeComponent = COMPONENT_RAID_TIME_REMAINING.apply(
                StringUtil.formatTickDuration((int) (RAID_TIMEOUT_TICKS - ticksActive), serverLevel.tickRateManager().tickrate()));

        return originalComponent.copy().append(" - ").append(timeComponent);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;DEBUG_RAIDS:Z", opcode = Opcodes.GETSTATIC))
    private void setRaidBarProgress(ServerLevel serverLevel, CallbackInfo ci, @Local int raiderCount) {
        if (raiderCount > 0)
            raidEvent.setProgress(ticksActive <= 20 ? 1 : 1 - (float) ticksActive / RAID_TIMEOUT_TICKS);
    }

    @Redirect(method = "updateBossbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;setProgress(F)V"))
    private void removeDefaultRaidBarProgress(ServerBossEvent instance, float progress) {
        // 미사용
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "longValue=48000"))
    private long modifyTimeoutCondition(long original) {
        return RAID_TIMEOUT_TICKS;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;hasMoreWaves()Z", ordinal = 0))
    private void resetTimeoutIfNoRaiders(ServerLevel serverLevel, CallbackInfo ci) {
        ticksActive = 0;
    }

    @Inject(method = "updateRaiders", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raider;isRemoved()Z"))
    private void resetTimeoutIfRaiderIsInVillage(ServerLevel serverLevel, CallbackInfo ci, @Local BlockPos raiderBlockPos) {
        if (serverLevel.isVillage(raiderBlockPos))
            ticksActive = 0;
    }

    @Expression("? <= 64.0")
    @ModifyExpressionValue(method = "playSound", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private boolean modifyRaidHornCondition(boolean original) {
        return true;
    }

    @ModifyArgs(method = "playSound", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/protocol/game/ClientboundSoundPacket;<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;DDDFFJ)V"))
    private void modifyRaidHornArgs(Args args, @Local(argsOnly = true) BlockPos raidPos) {
        args.set(1, SoundSource.HOSTILE);
        args.set(2, (double) raidPos.getX());
        args.set(3, (double) raidPos.getY());
        args.set(4, (double) raidPos.getZ());
        args.set(5, 1000F);
    }

    @Overwrite
    private void spawnGroup(ServerLevel serverLevel, BlockPos blockPos) {
        ticksActive = 0;
        totalHealth = 0;

        ReloadableDataManager.RAID_WAVE.get(serverLevel.getCurrentDifficultyAt(blockPos).getDifficulty()).ifPresent(raidWave ->
                spawnRaiders(serverLevel, blockPos, raidWave, groupsSpawned + 1));

        waveSpawnPos = Optional.empty();
        groupsSpawned++;
        setDirty(serverLevel);
    }
}
