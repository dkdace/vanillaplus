package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin<T extends BlockEntity> implements IForgeBlockEntity, VPMixin<T> {
    @Shadow
    @Final
    protected BlockPos worldPosition;
    @Shadow
    @Nullable
    protected Level level;

    @Shadow
    public abstract BlockState getBlockState();

    @Inject(method = "preRemoveSideEffects", at = @At("TAIL"))
    protected void onPreRemoveSideEffects(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        // 미사용
    }
}
