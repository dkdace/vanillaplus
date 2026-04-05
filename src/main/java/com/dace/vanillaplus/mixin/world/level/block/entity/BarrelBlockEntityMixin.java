package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.extension.world.level.block.VPLootContainerBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends RandomizableContainerBlockEntityMixin<BarrelBlockEntity> {
    @Unique
    private static final Component COMPONENT_BARREL_LOOT = Component.translatable("container.barrel_loot");

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadAdditional(ValueInput valueInput, CallbackInfo ci) {
        onLoadAdditional(valueInput);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveAdditional(ValueOutput valueOutput, CallbackInfo ci) {
        onSaveAdditional(valueOutput);
    }

    @Inject(method = "updateBlockState", at = @At("HEAD"), cancellable = true)
    private void cancelCloseIfAlwaysOpen(BlockState blockState, boolean isOpen, CallbackInfo ci) {
        if (!isOpen && blockState.getValue(VPLootContainerBlock.ALWAYS_OPEN))
            ci.cancel();
    }

    @ModifyReturnValue(method = "getDefaultName", at = @At(value = "RETURN"))
    private Component modifyDefaultName(Component component) {
        return getBlockState().getValue(VPLootContainerBlock.LOOT) ? COMPONENT_BARREL_LOOT : component;
    }
}
