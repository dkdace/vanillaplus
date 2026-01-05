package com.dace.vanillaplus.mixin.advancements.critereon;

import com.dace.vanillaplus.extension.advancements.critereon.VPPlayerPredicate;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(PlayerPredicate.class)
public abstract class PlayerPredicateMixin implements VPPlayerPredicate {
    @Shadow
    @Final
    public static final MapCodec<PlayerPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(PlayerPredicate::level),
                    GameTypePredicate.CODEC.optionalFieldOf("gamemode", GameTypePredicate.ANY).forGetter(PlayerPredicate::gameType),
                    PlayerPredicate.StatMatcher.CODEC.listOf().optionalFieldOf("stats", List.of()).forGetter(PlayerPredicate::stats),
                    ExtraCodecs.object2BooleanMap(Recipe.KEY_CODEC).optionalFieldOf("recipes", Object2BooleanMaps.emptyMap())
                            .forGetter(PlayerPredicate::recipes),
                    Codec.unboundedMap(ResourceLocation.CODEC, PlayerPredicate.AdvancementPredicate.CODEC)
                            .optionalFieldOf("advancements", Map.of()).forGetter(PlayerPredicate::advancements),
                    EntityPredicate.CODEC.optionalFieldOf("looking_at").forGetter(PlayerPredicate::lookingAt),
                    InputPredicate.CODEC.optionalFieldOf("input").forGetter(PlayerPredicate::input),
                    DistancePredicate.CODEC.optionalFieldOf("distance_to_respawn")
                            .forGetter(playerPredicate -> VPPlayerPredicate.cast(playerPredicate).getDistanceToRespawn()))
            .apply(instance, PlayerPredicateMixin::create));
    @Unique
    @Getter
    @Setter
    private Optional<DistancePredicate> distanceToRespawn;

    @Unique
    @NonNull
    private static PlayerPredicate create(MinMaxBounds.Ints level, GameTypePredicate gameType, List<PlayerPredicate.StatMatcher<?>> stats,
                                          Object2BooleanMap<ResourceKey<Recipe<?>>> recipes,
                                          Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements,
                                          Optional<EntityPredicate> lookingAt, Optional<InputPredicate> input,
                                          Optional<DistancePredicate> distanceToRespawn) {
        PlayerPredicate playerPredicate = new PlayerPredicate(level, gameType, stats, recipes, advancements, lookingAt, input);
        VPPlayerPredicate.cast(playerPredicate).setDistanceToRespawn(distanceToRespawn);

        return playerPredicate;
    }

    @Unique
    @Nullable
    private static BlockPos getValidRespawnBlockPos(@NonNull ServerPlayer serverPlayer) {
        ServerPlayer.RespawnConfig respawnConfig = serverPlayer.getRespawnConfig();
        if (respawnConfig == null)
            return null;

        GlobalPos respawnGlobalPos = respawnConfig.respawnData().globalPos();
        ServerLevel serverLevel = serverPlayer.level();
        if (respawnGlobalPos.dimension() != serverLevel.dimension())
            return null;

        BlockPos respawnPos = respawnGlobalPos.pos();
        BlockState blockState = serverLevel.getBlockState(respawnPos);
        Block block = blockState.getBlock();

        if ((!(block instanceof BedBlock) || !BedBlock.canSetSpawn(serverLevel)) && (!(block instanceof RespawnAnchorBlock)
                || !RespawnAnchorBlock.canSetSpawn(serverLevel) || blockState.getValue(RespawnAnchorBlock.CHARGE) == RespawnAnchorBlock.MIN_CHARGES))
            return null;

        return respawnPos;
    }

    @Inject(method = "matches", at = @At(value = "RETURN", ordinal = 8), cancellable = true)
    private void checkExtraConditions(Entity entity, ServerLevel serverLevel, Vec3 pos, CallbackInfoReturnable<Boolean> cir,
                                      @Local ServerPlayer serverPlayer) {
        if (distanceToRespawn.map(target -> {
            BlockPos respawnBlockPos = getValidRespawnBlockPos(serverPlayer);

            return respawnBlockPos == null || !target.matches(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), respawnBlockPos.getX(),
                    respawnBlockPos.getY(), respawnBlockPos.getZ());
        }).orElse(false))
            cir.setReturnValue(false);
    }
}
