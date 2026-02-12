package com.dace.vanillaplus.mixin.client.renderer;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin implements VPMixin<ItemInHandRenderer> {
    @ModifyExpressionValue(method = "renderArmWithItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/AbstractClientPlayer;isAutoSpinAttack()Z"))
    private boolean modifyAutoSpinAttackRenderCondition(boolean condition, @Local(argsOnly = true) ItemStack itemStack) {
        return condition && itemStack.is(Items.TRIDENT);
    }
}
