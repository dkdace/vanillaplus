package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.registryobject.VPBlockEntityTypes;
import com.dace.vanillaplus.extension.world.level.block.VPLayeredCauldronBlock;
import com.dace.vanillaplus.world.block.entity.WaterCauldronBlockEntity;
import com.dace.vanillaplus.world.block.modifier.BlockModifier;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin extends BlockMixin<LayeredCauldronBlock, BlockModifier> implements VPLayeredCauldronBlock<BlockModifier> {
    @Unique
    private static void addWater(@NonNull Level level, @NonNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
            waterCauldronBlockEntity.addPotion(null);
    }

    @Shadow
    protected abstract double getContentHeight(BlockState state);

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
        return getThis() == Blocks.WATER_CAULDRON ? new WaterCauldronBlockEntity(worldPosition, blockState) : null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState,
                                                                  @NonNull BlockEntityType<T> type) {
        if (type == VPBlockEntityTypes.WATER_CAULDRON.get())
            return (targetLevel, blockPos, targetBlockState, _) -> {
                if (targetLevel instanceof ServerLevel serverLevel && targetBlockState.getValue(UPDATE_COLOR))
                    serverLevel.setBlockAndUpdate(blockPos, blockState.setValue(UPDATE_COLOR, false));
            };

        return null;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity))
            return;

        int color = waterCauldronBlockEntity.getRandomColor(random);
        if (color == 0)
            return;

        double x = pos.getX() + 0.25 + random.nextDouble() * 0.5;
        double y = pos.getY() + getContentHeight(state);
        double z = pos.getZ() + 0.25 + random.nextDouble() * 0.5;

        level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color), x, y, z, 0, 0.2, 0);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/LayeredCauldronBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private BlockState modifyBlockState(BlockState state) {
        return state.setValue(UPDATE_COLOR, false);
    }

    @ModifyVariable(method = "createBlockStateDefinition", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"),
            argsOnly = true)
    private StateDefinition.Builder<Block, BlockState> modifyBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        return builder.add(UPDATE_COLOR);
    }

    @Inject(method = "handlePrecipitation", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private void addWaterOnRain(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation, CallbackInfo ci) {
        addWater(level, pos);
    }

    @Inject(method = "receiveStalactiteDrip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private void addWaterOnStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid, CallbackInfo ci) {
        addWater(level, pos);
    }
}
