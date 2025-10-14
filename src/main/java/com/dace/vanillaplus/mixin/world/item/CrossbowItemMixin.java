package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends ItemMixin<ItemModifier.CrossbowModifier> {
    @Redirect(method = "use", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/CrossbowItem;getShootingPower(Lnet/minecraft/world/item/component/ChargedProjectiles;)F"))
    private float getShootingPower(ChargedProjectiles chargedProjectiles) {
        Objects.requireNonNull(dataModifier);
        return chargedProjectiles.contains(Items.FIREWORK_ROCKET) ? dataModifier.getShootingPowerFireworkRocket() : dataModifier.getShootingPower();
    }

    @ModifyArg(method = "shootProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY(D)D"))
    private double modifyShootProjectileTargetY(double y) {
        return -0.25;
    }
}
