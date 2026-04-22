package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.item.ItemModifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends ItemMixin<BowItem, ItemModifier.ProjectileWeaponModifier> {
    @ModifyExpressionValue(method = "getPowerForTime", at = @At(value = "CONSTANT", args = "floatValue=3.0"))
    private static float modifyMaxPower(float power) {
        return VPModifiableData.getDataModifier(Items.BOW, ItemModifier.ProjectileWeaponModifier.class)
                .map(ItemModifier.ProjectileWeaponModifier::getShootingPower)
                .orElse(power);
    }

    @ModifyExpressionValue(method = "releaseUsing", at = @At(value = "CONSTANT", args = "floatValue=3.0"))
    private float modifyShootingPower(float shootingPower) {
        return getDataModifier().map(ItemModifier.ProjectileWeaponModifier::getShootingPower).orElse(shootingPower);
    }
}
