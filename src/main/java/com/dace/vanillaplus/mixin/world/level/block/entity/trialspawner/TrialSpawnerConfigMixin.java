package com.dace.vanillaplus.mixin.world.level.block.entity.trialspawner;

import com.dace.vanillaplus.extension.world.level.block.entity.trialspawner.VPTrialSpawnerConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TrialSpawnerConfig.class)
public abstract class TrialSpawnerConfigMixin implements VPTrialSpawnerConfig {
    @Shadow
    @Final
    public static TrialSpawnerConfig DEFAULT;
    @Shadow
    @Final
    public static final Codec<TrialSpawnerConfig> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange()).forGetter(TrialSpawnerConfig::spawnRange),
                    Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("total_mobs", DEFAULT.totalMobs())
                            .forGetter(TrialSpawnerConfig::totalMobs),
                    Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs())
                            .forGetter(TrialSpawnerConfig::simultaneousMobs),
                    Codec.floatRange(0, Float.MAX_VALUE)
                            .optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer())
                            .forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
                    Codec.floatRange(0, Float.MAX_VALUE)
                            .optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer())
                            .forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
                    Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn())
                            .forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
                    SpawnData.LIST_CODEC.optionalFieldOf("spawn_potentials", WeightedList.of())
                            .forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),
                    WeightedList.codec(LootTable.KEY_CODEC).optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject())
                            .forGetter(TrialSpawnerConfig::lootTablesToEject),
                    LootTable.KEY_CODEC.optionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous())
                            .forGetter(TrialSpawnerConfig::itemsToDropWhenOminous),
                    Ominous.CODEC.optionalFieldOf("ominous", Ominous.DEFAULT)
                            .forGetter(trialSpawnerConfig -> ((TrialSpawnerConfigMixin) (Object) trialSpawnerConfig).ominous))
            .apply(instance, TrialSpawnerConfigMixin::create));
    @Unique
    @NonNull
    @Getter
    private Ominous ominous = Ominous.DEFAULT;

    @Unique
    @NonNull
    private static TrialSpawnerConfig create(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer,
                                             float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn,
                                             WeightedList<SpawnData> spawnPotentialsDefinition, WeightedList<ResourceKey<LootTable>> lootTablesToEject,
                                             ResourceKey<LootTable> itemsToDropWhenOminous, Ominous ominous) {
        TrialSpawnerConfig trialSpawnerConfig = new TrialSpawnerConfig(spawnRange, totalMobs, simultaneousMobs, totalMobsAddedPerPlayer,
                simultaneousMobsAddedPerPlayer, ticksBetweenSpawn, spawnPotentialsDefinition, lootTablesToEject, itemsToDropWhenOminous);
        ((TrialSpawnerConfigMixin) (Object) trialSpawnerConfig).ominous = ominous;

        return trialSpawnerConfig;
    }
}
