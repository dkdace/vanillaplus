package com.dace.vanillaplus.mixin.advancements.criterion;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.FluidPredicate;
import net.minecraft.advancements.criterion.LightPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LocationPredicate.class)
public abstract class LocationPredicateMixin {
    @Shadow
    @Final
    public static final Codec<LocationPredicate> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(LocationPredicate.PositionPredicate.CODEC.optionalFieldOf("position").forGetter(LocationPredicate::position),
                    RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(LocationPredicate::biomes),
                    RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structures").forGetter(LocationPredicate::structures),
                    ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension").forGetter(LocationPredicate::dimension),
                    Codec.BOOL.optionalFieldOf("smokey").forGetter(LocationPredicate::smokey),
                    LightPredicate.CODEC.optionalFieldOf("light").forGetter(LocationPredicate::light),
                    BlockPredicate.CODEC.optionalFieldOf("block").forGetter(LocationPredicate::block),
                    FluidPredicate.CODEC.optionalFieldOf("fluid").forGetter(LocationPredicate::fluid),
                    Codec.BOOL.optionalFieldOf("can_see_sky").forGetter(LocationPredicate::canSeeSky),
                    Biome.Precipitation.CODEC.optionalFieldOf("precipitation")
                            .forGetter(locationPredicate -> ((LocationPredicateMixin) (Object) locationPredicate).precipitation))
            .apply(instance, LocationPredicateMixin::create));
    @Unique
    private Optional<Biome.Precipitation> precipitation = Optional.empty();

    @Unique
    @NonNull
    private static LocationPredicate create(Optional<LocationPredicate.PositionPredicate> position, Optional<HolderSet<Biome>> biomes,
                                            Optional<HolderSet<Structure>> structures, Optional<ResourceKey<Level>> dimension,
                                            Optional<Boolean> smokey, Optional<LightPredicate> light, Optional<BlockPredicate> block,
                                            Optional<FluidPredicate> fluid, Optional<Boolean> canSeeSky, Optional<Biome.Precipitation> precipitation) {
        LocationPredicate locationPredicate = new LocationPredicate(position, biomes, structures, dimension, smokey, light, block, fluid, canSeeSky);
        ((LocationPredicateMixin) (Object) locationPredicate).precipitation = precipitation;

        return locationPredicate;
    }

    @Inject(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isLoaded(Lnet/minecraft/core/BlockPos;)Z",
            shift = At.Shift.AFTER), cancellable = true)
    private void checkExtraConditions(ServerLevel level, double x, double y, double z, CallbackInfoReturnable<Boolean> cir,
                                      @Local(name = "pos") BlockPos pos) {
        if (precipitation.map(target -> !level.isLoaded(pos) || target != level.precipitationAt(pos)).orElse(false))
            cir.setReturnValue(false);
    }
}
