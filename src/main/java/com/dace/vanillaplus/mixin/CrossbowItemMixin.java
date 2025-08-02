package com.dace.vanillaplus.mixin;

import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CrossbowItem.class)
public final class CrossbowItemMixin {
    @Overwrite
    private static float getShootingPower(ChargedProjectiles chargedProjectiles) {
        return chargedProjectiles.contains(Items.FIREWORK_ROCKET) ? 1.8F : 4.3F;
    }

    @Overwrite
    public int getDefaultProjectileRange() {
        return 16;
    }

    @ModifyArg(method = "shootProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getY(D)D"))
    private double modifyShootProjectileTargetY(double y) {
        return -0.25;
    }
}
