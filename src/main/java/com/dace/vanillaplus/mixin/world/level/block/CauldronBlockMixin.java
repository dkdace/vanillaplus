package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.world.block.BlockModifier;
import com.dace.vanillaplus.world.block.entity.WaterCauldronBlockEntity;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronBlock.class)
public abstract class CauldronBlockMixin extends BlockMixin<CauldronBlock, BlockModifier> {
    @Unique
    private static void addWater(@NonNull Level level, @NonNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
            waterCauldronBlockEntity.addPotionContents(null);
    }

    @Inject(method = "handlePrecipitation", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;)V",
            ordinal = 0))
    private void addWaterOnRain(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation, CallbackInfo ci) {
        addWater(level, pos);
    }

    @Inject(method = "receiveStalactiteDrip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V",
            ordinal = 0))
    private void addWaterOnStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid, CallbackInfo ci) {
        addWater(level, pos);
    }
}
