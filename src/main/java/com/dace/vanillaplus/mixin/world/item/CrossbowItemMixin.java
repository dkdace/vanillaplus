package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.world.item.ItemModifier;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends ItemMixin<CrossbowItem, ItemModifier.CrossbowModifier> {
    @Shadow
    private static float getShootingPower(ChargedProjectiles chargedProjectiles) {
        return 0;
    }

    @Redirect(method = "use", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/CrossbowItem;getShootingPower(Lnet/minecraft/world/item/component/ChargedProjectiles;)F"))
    private float modifyShootingPower(ChargedProjectiles chargedProjectiles) {
        return getDataModifier()
                .map(crossbowModifier -> chargedProjectiles.contains(Items.FIREWORK_ROCKET)
                        ? crossbowModifier.getShootingPowerFireworkRocket()
                        : crossbowModifier.getShootingPower())
                .orElse(getShootingPower(chargedProjectiles));
    }

    @ModifyArg(method = "shootProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY(D)D"))
    private double modifyShootProjectileTargetYScale(double scale) {
        return getDataModifier().isPresent() ? -0.25 : scale;
    }
}
