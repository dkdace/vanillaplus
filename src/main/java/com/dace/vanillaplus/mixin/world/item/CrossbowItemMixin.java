package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.world.item.CrossbowConfig;
import com.dace.vanillaplus.world.item.ProjectileWeaponConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends ItemMixin<CrossbowItem> {
    @ModifyExpressionValue(method = "getShootingPower", at = @At(value = "CONSTANT", args = "floatValue=3.15"))
    private static float modifyArrowShootingPower(float power) {
        return ProjectileWeaponConfig.get(Items.CROSSBOW).shootingPower().orElse(power);
    }

    @ModifyExpressionValue(method = "getShootingPower", at = @At(value = "CONSTANT", args = "floatValue=1.6"))
    private static float modifyFireworkShootingPower(float power) {
        return CrossbowConfig.get().shootingPowerFireworkRocket().orElse(power);
    }

    @ModifyArg(method = "shootProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY(D)D"))
    private double modifyShootProjectileTargetYProgress(double progress) {
        return getDataModifier().isPresent() ? -0.25 : progress;
    }
}
