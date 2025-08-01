package com.dace.vanillaplus.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FireworkRocketEntity.class)
public final class FireworkRocketEntityMixin {
    @ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0))
    private void getDeltaMovement(Args args, @Local(ordinal = 0) Vec3 direction, @Local(ordinal = 1) Vec3 velocity) {
        args.set(0, direction.x * 0.1 + (direction.x * 1.2 - velocity.x) * 0.25);
        args.set(1, direction.y * 0.1 + (direction.y * 1.2 - velocity.y) * 0.25);
        args.set(2, direction.z * 0.1 + (direction.z * 1.2 - velocity.z) * 0.25);
    }
}
