package com.dace.vanillaplus.mixin.world.entity.vehicle.boat;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.EntityMixin;
import com.dace.vanillaplus.registryobject.VPAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AbstractBoat.class)
public abstract class AbstractBoatMixin<T extends AbstractBoat, U extends EntityModifier> extends EntityMixin<T, U> {
    @ModifyArgs(method = "controlBoat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private void modifySpeed(Args args) {
        LivingEntity controller = getControllingPassenger();
        if (controller == null)
            return;

        double speedMultiplier = controller.getAttributeValue(VPAttributes.VEHICLE_SPEED_MULTIPLIER.getHolder().orElseThrow());
        args.set(0, (double) args.get(0) * speedMultiplier);
        args.set(2, (double) args.get(2) * speedMultiplier);
    }
}
