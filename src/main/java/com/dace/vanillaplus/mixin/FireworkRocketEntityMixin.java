package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.rebalance.Rebalance;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {
    @ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0))
    private void modifyElytraDeltaMovement(Args args, @Local(ordinal = 0) Vec3 direction, @Local(ordinal = 1) Vec3 velocity) {
        args.setAll(direction.x * 0.1 + (direction.x * Rebalance.Elytra.FIREWORK_ADD_SPEED_MULTIPLIER - velocity.x)
                        * Rebalance.Elytra.FIREWORK_FINAL_SPEED_MULTIPLIER,
                direction.y * 0.1 + (direction.y * Rebalance.Elytra.FIREWORK_ADD_SPEED_MULTIPLIER - velocity.y)
                        * Rebalance.Elytra.FIREWORK_FINAL_SPEED_MULTIPLIER,
                direction.z * 0.1 + (direction.z * Rebalance.Elytra.FIREWORK_ADD_SPEED_MULTIPLIER - velocity.z)
                        * Rebalance.Elytra.FIREWORK_FINAL_SPEED_MULTIPLIER);
    }
}
