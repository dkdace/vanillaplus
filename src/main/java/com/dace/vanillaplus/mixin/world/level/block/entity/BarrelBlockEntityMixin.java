package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.custom.CustomBarrelBlockEntity;
import com.dace.vanillaplus.custom.CustomLootContainerBlock;
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
public abstract class BarrelBlockEntityMixin extends BlockEntityMixin implements CustomBarrelBlockEntity {
    @Unique
    private int vp$xp = 25;

    @Inject(method = "updateBlockState", at = @At("HEAD"), cancellable = true)
    private void cancelCloseIfAlwaysOpen(BlockState blockState, boolean isOpen, CallbackInfo ci) {
        if (!isOpen && blockState.getValue(CustomLootContainerBlock.vp$ALWAYS_OPEN))
            ci.cancel();
    }

    @ModifyReturnValue(method = "getDefaultName", at = @At(value = "RETURN"))
    private Component modifyDefaultName(Component component) {
        return getBlockState().getValue(CustomLootContainerBlock.vp$LOOT) ? Component.translatable("container.barrelLoot") : component;
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    protected void addLoadData(ValueInput valueInput, CallbackInfo ci) {
        vp$xp = valueInput.getIntOr("xp", vp$xp);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    protected void addSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt("xp", vp$xp);
    }

    @Override
    public int vp$getXp() {
        return vp$xp;
    }

    @Override
    public void vp$setXp(int xp) {
        vp$xp = xp;
    }
}
