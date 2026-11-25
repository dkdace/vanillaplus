package com.dace.vanillaplus.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin<T extends Mob, S extends HumanoidRenderState> extends LivingEntityRendererMixin<T, S> {
    @Inject(method = "getArmPose", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;",
            shift = At.Shift.AFTER), cancellable = true)
    private void setBlockArmPose(T mob, HumanoidArm arm, CallbackInfoReturnable<HumanoidModel.ArmPose> cir,
                                 @Local(name = "itemHeldByArm") ItemStack itemHeldByArm) {
        if (itemHeldByArm.getUseAnimation() == ItemUseAnimation.BLOCK)
            cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
    }
}
