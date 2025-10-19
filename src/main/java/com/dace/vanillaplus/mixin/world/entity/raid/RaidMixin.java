package com.dace.vanillaplus.mixin.world.entity.raid;

import com.dace.vanillaplus.data.RaidWave;
import com.dace.vanillaplus.data.modifier.GeneralModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow
    private int groupsSpawned;
    @Shadow
    private float totalHealth;
    @Shadow
    private Optional<BlockPos> waveSpawnPos;

    @Shadow
    public abstract void updateBossbar();

    @Shadow
    protected abstract void setDirty(ServerLevel serverLevel);

    @Shadow
    public abstract void setLeader(int wave, Raider raider);

    @Shadow
    public abstract void joinRaid(ServerLevel serverLevel, int wave, Raider raider, @Nullable BlockPos blockPos, boolean isSpawned);

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

    @Overwrite
    private void spawnGroup(ServerLevel serverLevel, BlockPos blockPos) {
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
