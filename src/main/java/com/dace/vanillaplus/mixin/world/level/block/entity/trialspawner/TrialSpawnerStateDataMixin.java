package com.dace.vanillaplus.mixin.world.level.block.entity.trialspawner;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.extension.world.level.block.entity.trialspawner.VPTrialSpawnerStateData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mixin(TrialSpawnerStateData.class)
public abstract class TrialSpawnerStateDataMixin implements VPTrialSpawnerStateData {
    @Shadow
    private Optional<ResourceKey<LootTable>> ejectingLootTable;
    @Unique
    @NonNull
    @Getter
    private Optional<Integer> trialOmenLevel = Optional.empty();

    @ModifyArg(method = "transformBadOmenIntoTrialOmen", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/core/Holder;II)V"), index = 1)
    private static int modifyTrialOmenDuration(int duration, @Local(name = "badOmen") MobEffectInstance badOmen) {
        return badOmen.getDuration();
    }

    @ModifyArg(method = "transformBadOmenIntoTrialOmen", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/core/Holder;II)V"), index = 2)
    private static int modifyTrialOmenAmplifier(int amplifier, @Local(name = "badOmen") MobEffectInstance badOmen) {
        return badOmen.getAmplifier();
    }

    @Inject(method = "lambda$tryDetectPlayers$0", at = @At("TAIL"))
    private static void setTrialOmenLevel(ServerLevel level, TrialSpawner trialSpawner, BlockPos pos, Pair<Player, Holder<MobEffect>> playerAndEffect,
                                          CallbackInfo ci, @Local(name = "player") Player player) {
        ((TrialSpawnerStateDataMixin) (Object) trialSpawner.getStateData()).trialOmenLevel = Optional.of(Math.clamp(Objects.requireNonNull(
                player.getEffect(MobEffects.TRIAL_OMEN)).getAmplifier() + 1L, 1, VPGameRules.MAX_POSSIBLE_BAD_OMEN_LEVEL));
    }

    @Inject(method = "reset", at = @At("TAIL"))
    private void reset(CallbackInfo ci) {
        ejectingLootTable = Optional.empty();
        trialOmenLevel = Optional.empty();
    }

    @WrapOperation(method = "pack", at = @At(value = "NEW",
            target = "(Ljava/util/Set;Ljava/util/Set;JJILjava/util/Optional;Ljava/util/Optional;)Lnet/minecraft/world/level/block/entity/trialspawner/TrialSpawnerStateData$Packed;"))
    private TrialSpawnerStateData.Packed redirectPack(Set<UUID> detectedPlayers, Set<UUID> currentMobs, long cooldownEndsAt, long nextMobSpawnsAt,
                                                      int totalMobsSpawned, Optional<SpawnData> nextSpawnData,
                                                      Optional<ResourceKey<LootTable>> ejectingLootTable,
                                                      Operation<TrialSpawnerStateData.Packed> original) {
        TrialSpawnerStateData.Packed packed = original.call(detectedPlayers, currentMobs, cooldownEndsAt, nextMobSpawnsAt, totalMobsSpawned,
                nextSpawnData, ejectingLootTable);
        Packed.cast(packed).setTrialOmenLevel(trialOmenLevel);

        return packed;
    }

    @Inject(method = "apply", at = @At("TAIL"))
    private void apply(TrialSpawnerStateData.Packed packed, CallbackInfo ci) {
        trialOmenLevel = Packed.cast(packed).getTrialOmenLevel();
    }

    @Mixin(TrialSpawnerStateData.Packed.class)
    public abstract static class PackedMixin implements Packed {
        @Shadow
        @Final
        public static final MapCodec<TrialSpawnerStateData.Packed> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(UUIDUtil.CODEC_SET.lenientOptionalFieldOf("registered_players", Set.of())
                                .forGetter(TrialSpawnerStateData.Packed::detectedPlayers),
                        UUIDUtil.CODEC_SET.lenientOptionalFieldOf("current_mobs", Set.of())
                                .forGetter(TrialSpawnerStateData.Packed::currentMobs),
                        Codec.LONG.lenientOptionalFieldOf("cooldown_ends_at", 0L)
                                .forGetter(TrialSpawnerStateData.Packed::cooldownEndsAt),
                        Codec.LONG.lenientOptionalFieldOf("next_mob_spawns_at", 0L)
                                .forGetter(TrialSpawnerStateData.Packed::nextMobSpawnsAt),
                        Codec.intRange(0, Integer.MAX_VALUE).lenientOptionalFieldOf("total_mobs_spawned", 0)
                                .forGetter(TrialSpawnerStateData.Packed::totalMobsSpawned),
                        SpawnData.CODEC.lenientOptionalFieldOf("spawn_data").forGetter(TrialSpawnerStateData.Packed::nextSpawnData),
                        LootTable.KEY_CODEC.lenientOptionalFieldOf("ejecting_loot_table")
                                .forGetter(TrialSpawnerStateData.Packed::ejectingLootTable),
                        ExtraCodecs.intRange(0, Byte.MAX_VALUE + 1).lenientOptionalFieldOf("trial_omen_level")
                                .forGetter(packed -> ((PackedMixin) (Object) packed).trialOmenLevel))
                .apply(instance, PackedMixin::create));
        @Unique
        @Getter
        @Setter
        private Optional<Integer> trialOmenLevel = Optional.empty();

        @Unique
        @NonNull
        private static TrialSpawnerStateData.Packed create(Set<UUID> detectedPlayers, Set<UUID> currentMobs, long cooldownEndsAt, long nextMobSpawnsAt,
                                                           int totalMobsSpawned, Optional<SpawnData> nextSpawnData,
                                                           Optional<ResourceKey<LootTable>> ejectingLootTable, Optional<Integer> trialOmenLevel) {
            TrialSpawnerStateData.Packed packed = new TrialSpawnerStateData.Packed(detectedPlayers, currentMobs, cooldownEndsAt, nextMobSpawnsAt,
                    totalMobsSpawned, nextSpawnData, ejectingLootTable);
            ((PackedMixin) (Object) packed).trialOmenLevel = trialOmenLevel;

            return packed;
        }
    }
}
