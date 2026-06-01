package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.world.entity.projectile.FireworkRocketConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends ProjectileMixin<FireworkRocketEntity> {
    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "doubleValue=1.5"))
    private double modifyFlightAddSpeedMultiplier(double power) {
        return FireworkRocketConfig.get().flightAddSpeedMultiplier().map(Float::doubleValue).orElse(power);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "doubleValue=0.5"),
            slice = @Slice(to = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getHandHoldingItemAngle(Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/phys/Vec3;")))
    private double modifyFlightFinalSpeedMultiplier(double power) {
        return FireworkRocketConfig.get().flightFinalSpeedModifier().map(Float::doubleValue).orElse(power);
    }
}
