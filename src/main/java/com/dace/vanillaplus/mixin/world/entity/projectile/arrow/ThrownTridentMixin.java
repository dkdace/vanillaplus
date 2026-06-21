package com.dace.vanillaplus.mixin.world.entity.projectile.arrow;

import com.dace.vanillaplus.world.item.TridentConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin extends AbstractArrowMixin<ThrownTrident> {
    @WrapWithCondition(method = "onHitEntity", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;dealtDamage:Z", opcode = Opcodes.PUTFIELD))
    private boolean addPiercedEntity(ThrownTrident instance, boolean value, @Local(name = "entity") Entity entity) {
        if (TridentConfig.get().projectilePiercing()) {
            addPiercedEntity(entity);
            return false;
        }

        return true;
    }

    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;deflect(Lnet/minecraft/world/entity/projectile/ProjectileDeflection;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityReference;Z)Z"))
    private boolean removeDeflect(ThrownTrident instance, ProjectileDeflection deflection, Entity deflectingEntity, EntityReference<Entity> newOwner,
                                  boolean byAttack, Operation<Boolean> original) {
        return !TridentConfig.get().projectilePiercing() && original.call(instance, deflection, deflectingEntity, newOwner, byAttack);
    }

    @WrapWithCondition(method = "onHitEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private boolean removeHitSlowDown(ThrownTrident instance, Vec3 deltaMovement) {
        return !TridentConfig.get().projectilePiercing();
    }
}
