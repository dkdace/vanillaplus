package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.block.LayeredCauldronBlockEntity;
import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.extension.world.level.block.VPLayeredCauldronBlock;
import com.dace.vanillaplus.registryobject.VPBlockEntityTypes;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin extends BlockMixin<LayeredCauldronBlock, BlockModifier> implements VPLayeredCauldronBlock {
    @Shadow
    protected abstract double getContentHeight(BlockState blockState);

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new LayeredCauldronBlockEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState,
                                                                  @NonNull BlockEntityType<T> blockEntityType) {
        if (blockEntityType == VPBlockEntityTypes.LAYERED_CAULDRON.get())
            return (targetLevel, blockPos, targetBlockState, blockEntity) -> {
                if (targetLevel instanceof ServerLevel serverLevel)
                    LayeredCauldronBlockEntity.serverTick(serverLevel, blockPos, targetBlockState, (LayeredCauldronBlockEntity) blockEntity);
            };

        return null;
    }

    @Override
    protected void onAnimateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        if (!(level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity))
            return;

        Holder<MobEffect> mobEffectHolder = layeredCauldronBlockEntity.getRandomMobEffect(randomSource);
        if (mobEffectHolder == null)
            return;

        int color = mobEffectHolder.value().getColor();
        float v = 0.75F + randomSource.nextFloat() * 0.25F;
        float alpha = ARGB.alphaFloat(layeredCauldronBlockEntity.getColor());
        float red = ARGB.redFloat(color) * v;
        float green = ARGB.greenFloat(color) * v;
        float blue = ARGB.blueFloat(color) * v;
        double x = blockPos.getCenter().x() + randomSource.triangle(0, 0.25);
        double y = blockPos.getY() + getContentHeight(blockState);
        double z = blockPos.getCenter().z() + randomSource.triangle(0, 0.25);

        level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.colorFromFloat(alpha, red, green, blue)), x, y, z, 0,
                0.2, 0);
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        return true;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/LayeredCauldronBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private BlockState modifyBlockState(BlockState blockState) {
        return blockState.setValue(OPACITY, 1).setValue(UPDATE_COLOR, false);
    }

    @ModifyVariable(method = "createBlockStateDefinition", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"),
            argsOnly = true)
    private StateDefinition.Builder<Block, BlockState> modifyBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        return builder.add(OPACITY, UPDATE_COLOR);
    }

    @Inject(method = "handlePrecipitation", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private void addWaterOnRain(BlockState blockState, Level level, BlockPos blockPos, Biome.Precipitation precipitation, CallbackInfo ci) {
        if (level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
            layeredCauldronBlockEntity.addPotionContents(null);
    }

    @Inject(method = "receiveStalactiteDrip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private void addWaterOnStalactiteDrip(BlockState blockState, Level level, BlockPos blockPos, Fluid fluid, CallbackInfo ci) {
        if (level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
            layeredCauldronBlockEntity.addPotionContents(null);
    }
}
