package com.dace.vanillaplus.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public final class MobMixin {
    @Inject(method = "setAggressive", at = @At("HEAD"))
    private void stopRidingIfAggressive(boolean isAggressive, CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;

        if (isAggressive && mob.getVehicle() instanceof VehicleEntity)
            mob.stopRiding();
    }

    @Inject(method = "startRiding", at = @At("HEAD"), cancellable = true)
    private void preventRidingIfAggressive(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        Mob mob = (Mob) (Object) this;

        if (mob.isAggressive() && vehicle instanceof VehicleEntity)
            cir.setReturnValue(false);
    }
}
