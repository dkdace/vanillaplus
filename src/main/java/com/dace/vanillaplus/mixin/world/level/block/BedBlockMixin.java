package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin extends BlockMixin<BlockModifier> {
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", ordinal = 0), cancellable = true)
    private void preventUseIfCannotUse(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult,
                                       CallbackInfoReturnable<InteractionResult> cir) {
        player.displayClientMessage(Component.translatable("block.minecraft.bed.no_use"), true);
        cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
}
