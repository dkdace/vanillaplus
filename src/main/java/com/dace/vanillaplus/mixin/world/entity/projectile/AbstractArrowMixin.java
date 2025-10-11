package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @ModifyExpressionValue(method = "doKnockback", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double modifyKnockbackStrength(double original, @Local(argsOnly = true) LivingEntity livingEntity,
                                           @Local(argsOnly = true) DamageSource damageSource) {
        return VPAttributes.getFinalKnockbackResistance(livingEntity, damageSource);
    }
}
