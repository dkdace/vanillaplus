package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.ReloadableDataManager;
import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.util.DynamicComponent;
import com.dace.vanillaplus.world.entity.raid.RaidWave;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(Raid.class)
public abstract class RaidMixin implements VPMixin<Raid> {
    @Unique
    private static final DynamicComponent COMPONENT_RAID_WAVES = args ->
            Component.translatable("event.minecraft.raid.waves", args);
    @Unique
    private static final DynamicComponent COMPONENT_RAID_TIME_REMAINING = args ->
            Component.translatable("event.minecraft.raid.time_remaining", args);
    @Unique
    private static final int TICK_RESET_THRESHOLD = 20;

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

    @Unique
    private static int getRaidTimeLimit(@NonNull ServerLevel serverLevel) {
        return VPGameRules.getValue(VPGameRules.RAID_TIME_LIMIT, serverLevel) * 20;
    }

    @Shadow
    protected abstract void setDirty(ServerLevel level);

    @Shadow
    public abstract void setLeader(int wave, Raider raider);

    @Shadow
    public abstract void joinRaid(ServerLevel level, int groupNumber, Raider raider, @Nullable BlockPos pos, boolean exists);

    @Shadow
    protected abstract boolean isFinalWave();

    @Shadow
    public abstract void updateBossbar();

    @Unique
    private void spawnRaiders(@NonNull ServerLevel serverLevel, @NonNull BlockPos blockPos, @NonNull RaidWave raidWave, int wave) {
        boolean hasLeader = false;

        for (RaidWave.RaiderGroup raiderGroup : raidWave.getRaiderGroups(wave))
            for (int i = 0; i < raiderGroup.count(); i++) {
                Entity entity = raiderGroup.entityType().create(serverLevel, EntitySpawnReason.EVENT);
                if (!(entity instanceof Raider raider))
                    continue;

                joinRaid(serverLevel, wave, raider, blockPos, false);

                if (!hasLeader && raider.canBeLeader()) {
                    raider.setPatrolLeader(true);
                    setLeader(wave, raider);
                    hasLeader = true;
                }

                raiderGroup.ridingEntityType().ifPresent(entityType -> spawnRider(serverLevel, blockPos, wave, entityType, raider));
            }
    }

    @Unique
    private void spawnRider(@NonNull ServerLevel serverLevel, @NonNull BlockPos blockPos, int wave, @NonNull EntityType<?> entityType,
                            @NonNull Raider vehicle) {
        Entity rider = entityType.create(serverLevel, EntitySpawnReason.EVENT);
        if (rider == null)
            return;

        if (rider instanceof Raider raider)
            joinRaid(serverLevel, wave, raider, blockPos, false);

        rider.snapTo(blockPos, 0, 0);
        rider.startRiding(vehicle);
    }

    @ModifyArg(method = "absorbRaidOmen", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"), index = 2)
    private int modifyMaxRaidOmenLevel(int max, @Local(argsOnly = true) ServerPlayer player) {
        return VPGameRules.getValue(VPGameRules.MAX_BAD_OMEN_LEVEL, player.level());
    }

    @ModifyReturnValue(method = "hasBonusWave", at = @At("RETURN"))
    private boolean removeBonusWave(boolean hasBonusWave) {
        return false;
    }

    @ModifyReturnValue(method = "getNumGroups", at = @At("RETURN"))
    public int modifyNumGroups(int numGroups, @Local(argsOnly = true) Difficulty difficulty) {
        return ReloadableDataManager.RAID_WAVE.get(difficulty).map(RaidWave::getTotalWaves).orElse(numGroups);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/raid/Raid;RAID_NAME_COMPONENT:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETSTATIC))
    private Component modifyRaidBarName(Component component, @Local(name = "raidersAlive") int raidersAlive) {
        int wave = groupsSpawned;
        if (raidersAlive == 0 && !isFinalWave())
            wave++;

        return COMPONENT_RAID_WAVES.get(wave, numGroups);
    }

    @Definition(id = "raidersAlive", local = @Local(type = int.class, name = "raidersAlive"))
    @Expression("raidersAlive <= 2")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyRaiderCountingCondition(boolean original, @Local(argsOnly = true) ServerLevel level) {
        return getRaidTimeLimit(level) <= 0 || ticksActive <= TICK_RESET_THRESHOLD;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerBossEvent;setName(Lnet/minecraft/network/chat/Component;)V", ordinal = 2))
    private Component addRaidBarTimer(Component component, @Local(argsOnly = true) ServerLevel level) {
        int raidTime = getRaidTimeLimit(level);
        if (raidTime <= 0)
            return component;

        Component timeComponent = COMPONENT_RAID_TIME_REMAINING.get(
                StringUtil.formatTickDuration((int) (raidTime - ticksActive), level.tickRateManager().tickrate()));

        return component.copy().append(" - ").append(timeComponent);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;DEBUG_RAIDS:Z", opcode = Opcodes.GETSTATIC))
    private void setRaidBarTimerProgress(ServerLevel level, CallbackInfo ci, @Local(name = "raidersAlive") int raidersAlive) {
        int raidTime = getRaidTimeLimit(level);
        if (raidTime > 0 && raidersAlive > 0)
            raidEvent.setProgress(ticksActive <= TICK_RESET_THRESHOLD ? 1 : 1 - (float) ticksActive / raidTime);
    }

    @WrapWithCondition(method = {"spawnGroup", "removeFromRaid", "addWaveMob(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/entity/raid/Raider;Z)Z"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;updateBossbar()V"))
    private boolean removeUpdateBossbarCall(Raid instance, @Local(argsOnly = true) ServerLevel level) {
        return getRaidTimeLimit(level) <= 0;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "longValue=48000"))
    private long modifyTimeoutCondition(long original, @Local(argsOnly = true) ServerLevel level) {
        int raidTime = getRaidTimeLimit(level);
        return raidTime <= 0 ? original : raidTime;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;hasMoreWaves()Z", ordinal = 0))
    private void resetTicksIfNoRaiders(ServerLevel level, CallbackInfo ci) {
        if (getRaidTimeLimit(level) > 0 && ticksActive > TICK_RESET_THRESHOLD)
            ticksActive = 0;
    }

    @Inject(method = "updateRaiders", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raider;isRemoved()Z"))
    private void resetTicksIfRaiderIsInVillage(ServerLevel level, CallbackInfo ci, @Local(name = "raiderPos") BlockPos raiderPos) {
        if (getRaidTimeLimit(level) > 0 && level.isVillage(raiderPos))
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

    @Inject(method = "spawnGroup", at = @At("HEAD"), cancellable = true)
    private void spawnGroup(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        ReloadableDataManager.RAID_WAVE.get(level.getCurrentDifficultyAt(pos).getDifficulty()).ifPresent(raidWave -> {
            ticksActive = 0;
            totalHealth = 0;

            spawnRaiders(level, pos, raidWave, groupsSpawned + 1);

            waveSpawnPos = Optional.empty();
            groupsSpawned++;
            setDirty(level);

            if (getRaidTimeLimit(level) <= 0)
                updateBossbar();

            ci.cancel();
        });
    }
}
