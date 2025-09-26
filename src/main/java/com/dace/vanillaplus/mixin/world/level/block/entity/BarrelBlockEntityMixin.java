package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends RandomizableContainerBlockEntityMixin {
    @Inject(method = "updateBlockState", at = @At("HEAD"), cancellable = true)
    private void cancelCloseIfAlwaysOpen(BlockState blockState, boolean isOpen, CallbackInfo ci) {
        if (!isOpen && blockState.getValue(CustomLootContainerBlock.ALWAYS_OPEN))
            ci.cancel();
    }

    @ModifyReturnValue(method = "getDefaultName", at = @At(value = "RETURN"))
    private Component modifyDefaultName(Component component) {
        return getBlockState().getValue(CustomLootContainerBlock.LOOT) ? Component.translatable("container.barrelLoot") : component;
    }
}
