package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.registryobject.VPBlockEntityTypes;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.dace.vanillaplus.extension.world.level.block.VPLayeredCauldronBlock;
import com.dace.vanillaplus.world.block.BlockModifier;
import com.dace.vanillaplus.world.block.entity.WaterCauldronBlockEntity;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin extends BlockMixin<LayeredCauldronBlock, BlockModifier> implements VPLayeredCauldronBlock<BlockModifier> {
    @Shadow
    @Final
    public static IntegerProperty LEVEL;

    @Inject(method = "lowerFillLevel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private static void onLowerFillLevel(BlockState blockState, Level level, BlockPos blockPos, CallbackInfo ci) {
        if (VPBlock.cast(blockState.getBlock()).getDataModifier().isPresent()
                && level.getBlockEntity(blockPos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
            waterCauldronBlockEntity.onLowerFillLevel();
    }

    @Shadow
    public abstract double getContentHeight(BlockState blockState);

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return getThis() == Blocks.WATER_CAULDRON ? new WaterCauldronBlockEntity(blockPos, blockState) : null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState,
                                                                  @NonNull BlockEntityType<T> blockEntityType) {
        if (blockEntityType == VPBlockEntityTypes.WATER_CAULDRON.get())
            return (targetLevel, blockPos, targetBlockState, _) -> {
                if (targetLevel instanceof ServerLevel serverLevel && targetBlockState.getValue(UPDATE_COLOR))
                    serverLevel.setBlockAndUpdate(blockPos, blockState.setValue(UPDATE_COLOR, false));
            };

        return null;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (VPBlock.cast(blockState.getBlock()).getDataModifier().isEmpty()
                || !(level.getBlockEntity(blockPos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity))
            return;

        Holder<MobEffect> mobEffectHolder = waterCauldronBlockEntity.getRandomMobEffect(randomSource);
        if (mobEffectHolder == null)
            return;

        int color = mobEffectHolder.value().getColor();
        float v = 0.75F + randomSource.nextFloat() * 0.25F;
        float alpha = ARGB.alphaFloat(waterCauldronBlockEntity.getColor());
        float red = ARGB.redFloat(color) * v;
        float green = ARGB.greenFloat(color) * v;
        float blue = ARGB.blueFloat(color) * v;
        double x = blockPos.getCenter().x() + randomSource.triangle(0, 0.25);
        double y = blockPos.getY() + getContentHeight(blockState);
        double z = blockPos.getCenter().z() + randomSource.triangle(0, 0.25);

        level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.colorFromFloat(alpha, red, green, blue)), x, y, z, 0,
                0.2, 0);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/LayeredCauldronBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private BlockState modifyBlockState(BlockState blockState) {
        return blockState.setValue(UPDATE_COLOR, false);
    }

    @ModifyVariable(method = "createBlockStateDefinition", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"),
            argsOnly = true)
    private StateDefinition.Builder<Block, BlockState> modifyBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        return builder.add(UPDATE_COLOR);
    }

    @Inject(method = "handlePrecipitation", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private void addWaterOnRain(BlockState blockState, Level level, BlockPos blockPos, Biome.Precipitation precipitation, CallbackInfo ci) {
        if (VPBlock.cast(blockState.getBlock()).getDataModifier().isPresent()
                && level.getBlockEntity(blockPos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
            waterCauldronBlockEntity.addPotionContents(null);
    }

    @Inject(method = "receiveStalactiteDrip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private void addWaterOnStalactiteDrip(BlockState blockState, Level level, BlockPos blockPos, Fluid fluid, CallbackInfo ci) {
        if (VPBlock.cast(blockState.getBlock()).getDataModifier().isPresent()
                && level.getBlockEntity(blockPos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
            waterCauldronBlockEntity.addPotionContents(null);
    }

    @Inject(method = "handleEntityOnFireInside", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/LayeredCauldronBlock;lowerFillLevel(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void addWaterOnPowderSnowMelt(BlockState blockState, Level level, BlockPos blockPos, CallbackInfo ci) {
        if (VPBlock.cast(blockState.getBlock()).getDataModifier().isPresent())
            for (int i = 0; i < level.getBlockState(blockPos).getValue(LEVEL); i++)
                if (level.getBlockEntity(blockPos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
                    waterCauldronBlockEntity.addPotionContents(null);
    }
}
