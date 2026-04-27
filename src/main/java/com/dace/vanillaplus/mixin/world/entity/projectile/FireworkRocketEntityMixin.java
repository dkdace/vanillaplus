package com.dace.vanillaplus.mixin.world.entity.projectile;

import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.dace.vanillaplus.world.item.ItemModifier;
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
    private void modifyElytraDeltaMovement(Args args, @Local(name = "lookAngle") Vec3 lookAngle, @Local(name = "movement") Vec3 movement) {
        VPModifiableData.getDataModifier(Items.ELYTRA, ItemModifier.ElytraModifier.class).ifPresent(elytraModifier ->
                args.setAll(lookAngle.x() * 0.1 + (lookAngle.x() * elytraModifier.getFireworkAddSpeedMultiplier() - movement.x())
                                * elytraModifier.getFireworkFinalSpeedModifier(),
                        lookAngle.y() * 0.1 + (lookAngle.y() * elytraModifier.getFireworkAddSpeedMultiplier() - movement.y())
                                * elytraModifier.getFireworkFinalSpeedModifier(),
                        lookAngle.z() * 0.1 + (lookAngle.z() * elytraModifier.getFireworkAddSpeedMultiplier() - movement.z())
                                * elytraModifier.getFireworkFinalSpeedModifier()));
    }
}
