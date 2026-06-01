package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.world.item.ProjectileWeaponConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends ItemMixin<BowItem> {
    @ModifyExpressionValue(method = "getPowerForTime", at = @At(value = "CONSTANT", args = "floatValue=3.0"))
    private static float modifyMaxPower(float power) {
        return ProjectileWeaponConfig.get(Items.BOW).shootingPower().orElse(power);
    }

    @ModifyExpressionValue(method = "releaseUsing", at = @At(value = "CONSTANT", args = "floatValue=3.0"))
    private float modifyShootingPower(float shootingPower) {
        return ProjectileWeaponConfig.get(getThis()).shootingPower().orElse(shootingPower);
    }
}
