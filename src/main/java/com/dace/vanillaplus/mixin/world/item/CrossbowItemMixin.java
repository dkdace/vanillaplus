package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends ItemMixin<ItemModifier.CrossbowModifier> {
    @Redirect(method = "use", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/CrossbowItem;getShootingPower(Lnet/minecraft/world/item/component/ChargedProjectiles;)F"))
    private float getShootingPower(ChargedProjectiles chargedProjectiles, @Local(argsOnly = true) Level level) {
        Objects.requireNonNull(dataModifier);

        return chargedProjectiles.contains(Items.FIREWORK_ROCKET)
                ? dataModifier.getShootingPowerFireworkRocket()
                : dataModifier.getShootingPowerArrow();
    }

    @ModifyArg(method = "shootProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY(D)D"))
    private double modifyShootProjectileTargetY(double y) {
        return -0.25;
    }
}
