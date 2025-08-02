package com.dace.vanillaplus.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public final class BedBlockMixin {
    @Overwrite
    public static boolean canSetSpawn(Level level) {
        return true;
    }

    @Inject(method = "useWithoutItem", at = @At("RETURN"), cancellable = true)
    private void preventUseInOtherDimension(BlockState blockState, Level level, BlockPos blockPos, Player player,
                                            BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.dimension() == Level.OVERWORLD)
            return;

        player.displayClientMessage(Component.translatable("block.vanillaplus.bed.no_use"), true);
        cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
}
