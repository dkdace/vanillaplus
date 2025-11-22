package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends ItemMixin<BowItem, ItemModifier.ProjectileWeaponModifier> {
    @ModifyExpressionValue(method = "getPowerForTime", at = @At(value = "CONSTANT", args = "floatValue=3.0"))
    private static float modifyMaxPower(float original) {
        ItemModifier.ProjectileWeaponModifier projectileWeaponModifier = ItemModifier.fromItemOrThrow(Items.BOW);
        return projectileWeaponModifier.getShootingPower();
    }

    @ModifyExpressionValue(method = "releaseUsing", at = @At(value = "CONSTANT", args = "floatValue=3.0"))
    private float modifyShootingPower(float shootingPower) {
        return Objects.requireNonNull(dataModifier).getShootingPower();
    }
}
