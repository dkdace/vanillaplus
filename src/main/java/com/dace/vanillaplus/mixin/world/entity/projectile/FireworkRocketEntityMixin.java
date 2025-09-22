package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.mixin.world.entity.EntityMixin;
import com.dace.vanillaplus.rebalance.modifier.DataModifiers;
import com.dace.vanillaplus.rebalance.modifier.ItemModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends EntityMixin {
    @ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0))
    private void modifyElytraDeltaMovement(Args args, @Local(ordinal = 0) Vec3 direction, @Local(ordinal = 1) Vec3 velocity) {
        ItemModifier.ElytraModifier elytraModifier = (ItemModifier.ElytraModifier) DataModifiers.get(level().registryAccess(),
                DataModifiers.ITEM_MODIFIER_MAP, Items.ELYTRA);

        args.setAll(direction.x * 0.1 + (direction.x * elytraModifier.getFireworkAddSpeedMultiplier() - velocity.x)
                        * elytraModifier.getFireworkFinalSpeedModifier(),
                direction.y * 0.1 + (direction.y * elytraModifier.getFireworkAddSpeedMultiplier() - velocity.y)
                        * elytraModifier.getFireworkFinalSpeedModifier(),
                direction.z * 0.1 + (direction.z * elytraModifier.getFireworkAddSpeedMultiplier() - velocity.z)
                        * elytraModifier.getFireworkFinalSpeedModifier());
    }
}
