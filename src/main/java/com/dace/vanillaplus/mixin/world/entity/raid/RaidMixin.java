package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.GeneralConfig;
import com.dace.vanillaplus.data.RaidWave;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Map;
import java.util.Optional;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Unique
    private static final String COMPONENT_RAID_WAVES = "event.minecraft.raid.waves";
    @Unique
    private static final String COMPONENT_RAID_TIME_REMAINING = "event.minecraft.raid.time_remaining";
    @Shadow
    @Final
    private static final int RAID_TIMEOUT_TICKS = 3 * 60 * 20;

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
    public abstract void updateBossbar();

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

    @Overwrite
    public int getMaxRaidOmenLevel() {
        return GeneralConfig.get().getMaxBadOmenLevel();
    }

    @Overwrite
    private boolean hasBonusWave() {
        return false;
    }

    @Overwrite
    public int getNumGroups(Difficulty difficulty) {
        RaidWave raidWave = RaidWave.fromDifficulty(difficulty);
        return raidWave == null ? 0 : raidWave.getTotalWaves();
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/raid/Raid;RAID_NAME_COMPONENT:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETSTATIC))
    private Component modifyRaidBarName(Component originalComponent, @Local(argsOnly = true) ServerLevel serverLevel, @Local int raiderCount) {
        int wave = groupsSpawned;
        if (raiderCount == 0 && !isFinalWave())
            wave++;

        MutableComponent component = Component.translatable(COMPONENT_RAID_WAVES, wave, numGroups);
        if (ticksActive > 20) {
            MutableComponent timeComponent = Component.translatable(COMPONENT_RAID_TIME_REMAINING,
                    StringUtil.formatTickDuration((int) (RAID_TIMEOUT_TICKS - ticksActive), serverLevel.tickRateManager().tickrate()));

            component.append(" - ").append(timeComponent);
        }

        return component;
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
    private void modifyRaidHornPosition(Args args, @Local(argsOnly = true) BlockPos raidPos) {
        args.set(2, (double) raidPos.getX());
        args.set(3, (double) raidPos.getY());
        args.set(4, (double) raidPos.getZ());
        args.set(5, 1000F);
    }

    @Overwrite
    private void spawnGroup(ServerLevel serverLevel, BlockPos blockPos) {
        ticksActive = 0;
        totalHealth = 0;

        RaidWave raidWave = RaidWave.fromDifficulty(serverLevel.getCurrentDifficultyAt(blockPos).getDifficulty());
        if (raidWave == null)
            return;

        spawnRaiders(serverLevel, blockPos, raidWave, groupsSpawned + 1);

        waveSpawnPos = Optional.empty();
        groupsSpawned++;
        updateBossbar();
        setDirty(serverLevel);
    }
}
