package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends ProjectileMixin<FireworkRocketEntity, EntityModifier> {
    @ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0))
    private void modifyElytraDeltaMovement(Args args, @Local(ordinal = 0) Vec3 direction, @Local(ordinal = 1) Vec3 speed) {
        VPModifiableData.getDataModifier(Items.ELYTRA, ItemModifier.ElytraModifier.class).ifPresent(elytraModifier ->
                args.setAll(direction.x() * 0.1 + (direction.x() * elytraModifier.getFireworkAddSpeedMultiplier() - speed.x())
                                * elytraModifier.getFireworkFinalSpeedModifier(),
                        direction.y() * 0.1 + (direction.y() * elytraModifier.getFireworkAddSpeedMultiplier() - speed.y())
                                * elytraModifier.getFireworkFinalSpeedModifier(),
                        direction.z() * 0.1 + (direction.z() * elytraModifier.getFireworkAddSpeedMultiplier() - speed.z())
                                * elytraModifier.getFireworkFinalSpeedModifier()));
    }
}
