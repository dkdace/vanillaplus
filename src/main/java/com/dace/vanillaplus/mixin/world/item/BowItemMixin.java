package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends ItemMixin<ItemModifier.ProjectileWeaponModifier> {
    @Expression("3.0")
    @ModifyExpressionValue(method = "getPowerForTime", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float modifyMaxPower(float original) {
        ItemModifier.ProjectileWeaponModifier projectileWeaponModifier = Objects.requireNonNull(VPModifiableData.getDataModifier(Items.BOW));
        return projectileWeaponModifier.getShootingPower();
    }

    @Expression("3.0")
    @ModifyExpressionValue(method = "releaseUsing", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float modifyShootingPower(float shootingPower) {
        return Objects.requireNonNull(dataModifier).getShootingPower();
    }
}
