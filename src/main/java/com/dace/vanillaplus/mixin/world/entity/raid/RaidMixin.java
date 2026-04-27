package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.ReloadableDataManager;
import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.util.DynamicComponent;
import com.dace.vanillaplus.world.entity.raid.RaidWave;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

@Mixin(Raid.class)
public abstract class RaidMixin implements VPMixin<Raid> {
    @Unique
    private static final DynamicComponent COMPONENT_RAID_WAVES = args ->
            Component.translatable("event.minecraft.raid.waves", args);
    @Unique
    private static final DynamicComponent COMPONENT_RAID_TIME_REMAINING = args ->
            Component.translatable("event.minecraft.raid.time_remaining", args);
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
    protected abstract void setDirty(ServerLevel level);

    @Shadow
    public abstract void setLeader(int wave, Raider raider);

    @Shadow
    public abstract void joinRaid(ServerLevel level, int groupNumber, Raider raider, @Nullable BlockPos pos, boolean exists);

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
    private int modifyMaxRaidOmenLevel(int max, @Local(argsOnly = true) ServerPlayer player) {
        return VPGameRules.getValue(VPGameRules.MAX_BAD_OMEN_LEVEL, player.level());
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
    private Component modifyRaidBarName(Component component, @Local int raiderCount) {
        int wave = groupsSpawned;
        if (raiderCount == 0 && !isFinalWave())
            wave++;

        return COMPONENT_RAID_WAVES.get(wave, numGroups);
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerBossEvent;setName(Lnet/minecraft/network/chat/Component;)V", ordinal = 2))
    private Component addRaidBarTimer(Component component, @Local(argsOnly = true) ServerLevel level) {
        Component timeComponent = COMPONENT_RAID_TIME_REMAINING.get(
                StringUtil.formatTickDuration((int) (RAID_TIMEOUT_TICKS - ticksActive), level.tickRateManager().tickrate()));

        return component.copy().append(" - ").append(timeComponent);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;DEBUG_RAIDS:Z", opcode = Opcodes.GETSTATIC))
    private void setRaidBarProgress(ServerLevel level, CallbackInfo ci, @Local(name = "raidersAlive") int raidersAlive) {
        if (raidersAlive > 0)
            raidEvent.setProgress(ticksActive <= 20 ? 1 : 1 - (float) ticksActive / RAID_TIMEOUT_TICKS);
    }

    @Redirect(method = "updateBossbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;setProgress(F)V"))
    private void removeDefaultRaidBarProgress(ServerBossEvent instance, float progress) {
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
    private void modifyRaidHornArgs(Args args, @Local(argsOnly = true) BlockPos soundOrigin) {
        args.set(1, SoundSource.HOSTILE);
        args.set(2, (double) soundOrigin.getX());
        args.set(3, (double) soundOrigin.getY());
        args.set(4, (double) soundOrigin.getZ());
        args.set(5, 1000F);
    }

    @Overwrite
    private void spawnGroup(ServerLevel level, BlockPos pos) {
        ticksActive = 0;
        totalHealth = 0;

        ReloadableDataManager.RAID_WAVE.get(level.getCurrentDifficultyAt(pos).getDifficulty()).ifPresent(raidWave ->
                spawnRaiders(level, pos, raidWave, groupsSpawned + 1));

        waveSpawnPos = Optional.empty();
        groupsSpawned++;
        setDirty(level);
    }
}
