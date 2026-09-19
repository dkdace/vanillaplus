package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.extension.world.level.block.entity.VPPotentSulfurBlockEntity;
import com.dace.vanillaplus.extension.world.level.block.state.properties.VPPotentSulfurState;
import com.dace.vanillaplus.world.block.PotentSulfurConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PotentSulfurBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PotentSulfurState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotentSulfurBlock.class)
public abstract class PotentSulfurBlockMixin extends BlockMixin<PotentSulfurBlock> {
    @Shadow
    @Final
    public static EnumProperty<PotentSulfurState> STATE;

    @ModifyReturnValue(method = "validBlockState", at = @At(value = "RETURN", ordinal = 0))
    private static BlockState setBurningState(BlockState original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) LevelReader level,
                                              @Local(argsOnly = true) BlockPos pos) {
        BlockPos blockPos = pos.above();
        return PotentSulfurConfig.get().canBurn() && (level.getBlockState(blockPos).is(Blocks.FIRE) || level.getFluidState(blockPos).is(FluidTags.LAVA))
                ? state.setValue(STATE, VPPotentSulfurState.BURNING.get())
                : original;
    }

    @Inject(method = "getTicker", at = @At("HEAD"), cancellable = true)
    private void addBurningTicker(Level level, BlockState blockState, BlockEntityType<PotentSulfurBlockEntity> type,
                                  CallbackInfoReturnable<BlockEntityTicker<PotentSulfurBlockEntity>> cir) {
        if (blockState.getValue(STATE) != VPPotentSulfurState.BURNING.get())
            return;

        if (type == BlockEntityTypes.POTENT_SULFUR)
            cir.setReturnValue(level.isClientSide()
                    ? VPPotentSulfurBlockEntity.BURNING_CLIENT_TICKER.get()
                    : VPPotentSulfurBlockEntity.BURNING_SERVER_TICKER.get());
        else
            cir.setReturnValue(null);
    }

    @Inject(method = "animateTick", at = @At("TAIL"))
    private void playBurningSound(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (state.getValue(STATE) == VPPotentSulfurState.BURNING.get() && level.getFluidState(pos.above()).is(Fluids.LAVA))
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOXIOUS_GAS, SoundSource.AMBIENT, 1, 0.8F,
                    false);
    }
}
