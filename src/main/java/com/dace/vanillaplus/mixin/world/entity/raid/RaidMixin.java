package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.RaidWave;
import com.dace.vanillaplus.data.modifier.GeneralModifier;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Map;
import java.util.Optional;

@Mixin(Raid.class)
public abstract class RaidMixin {
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
    public abstract void joinRaid(ServerLevel serverLevel, int wave, Raider raider, @Nullable BlockPos blockPos, boolean isSpawned);

    @Shadow
    protected abstract boolean isFinalWave();

    @Overwrite
    public int getMaxRaidOmenLevel() {
        return GeneralModifier.get().getMaxBadOmenLevel();
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
            target = "Lnet/minecraft/world/entity/raid/Raid;RAID_NAME_COMPONENT:Lnet/minecraft/network/chat/Component;"))
    private Component modifyRaidBarName(Component originalComponent, @Local(argsOnly = true) ServerLevel serverLevel, @Local int raiderCount) {
        int wave = groupsSpawned;
        if (raiderCount == 0 && !isFinalWave())
            wave++;

        MutableComponent component = Component.translatable("event.minecraft.raid.waves", wave, numGroups);
        if (ticksActive > 20) {
            MutableComponent timeComponent = Component.translatable("event.minecraft.raid.time_remaining",
                    StringUtil.formatTickDuration((int) (RAID_TIMEOUT_TICKS - ticksActive), serverLevel.tickRateManager().tickrate()));

            component.append(" - ").append(timeComponent);
        }

        return component;
    }

    @Expression("48000")
    @ModifyExpressionValue(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private long modifyTimeoutCondition(long original) {
        return RAID_TIMEOUT_TICKS;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;hasMoreWaves()Z", ordinal = 0))
    private void resetTimeoutIfNoRaiders(ServerLevel serverLevel, CallbackInfo ci) {
        ticksActive = 0;
    }

    @Inject(method = "updateRaiders", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raider;isRemoved()Z"))
    private void resetTimeoutIfRaiderIsInVillage(ServerLevel serverLevel, CallbackInfo ci, @Local Raider raider, @Local BlockPos raiderBlockPos) {
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

        boolean hasLeader = false;
        int wave = groupsSpawned + 1;
        Map<RaidWave.RaiderType, Integer> raiderCountMap = raidWave.getRaiderCountMap(wave);

        for (Map.Entry<RaidWave.RaiderType, Integer> entry : raiderCountMap.entrySet()) {
            for (int j = 0; j < entry.getValue(); j++) {
                Raider raider = entry.getKey().getEntityType().create(serverLevel, EntitySpawnReason.EVENT);
                if (raider == null)
                    break;

                joinRaid(serverLevel, wave, raider, blockPos, false);

                if (!hasLeader && raider.canBeLeader()) {
                    raider.setPatrolLeader(true);
                    setLeader(wave, raider);
                    hasLeader = true;
                }

                EntityType<? extends Raider> ridingEntityType = entry.getKey().getRidingEntityType();
                if (ridingEntityType != null) {
                    Raider ridingRaider = ridingEntityType.create(serverLevel, EntitySpawnReason.EVENT);

                    if (ridingRaider != null) {
                        joinRaid(serverLevel, wave, ridingRaider, blockPos, false);

                        ridingRaider.snapTo(blockPos, 0, 0);
                        ridingRaider.startRiding(raider);
                    }
                }
            }
        }

        waveSpawnPos = Optional.empty();
        groupsSpawned++;
        updateBossbar();
        setDirty(serverLevel);
    }
}
