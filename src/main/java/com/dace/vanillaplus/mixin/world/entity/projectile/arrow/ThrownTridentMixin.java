package com.dace.vanillaplus.mixin.world.entity.projectile.arrow;

import com.dace.vanillaplus.world.entity.EntityModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin extends AbstractArrowMixin<ThrownTrident, EntityModifier> {
    @Redirect(method = "onHitEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;dealtDamage:Z",
            opcode = Opcodes.PUTFIELD))
    private void addPiercedEntity(ThrownTrident instance, boolean value, @Local(ordinal = 0) Entity target) {
        addPiercedEntity(target);
    }

    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;deflect(Lnet/minecraft/world/entity/projectile/ProjectileDeflection;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityReference;Z)Z"))
    private boolean removeDeflect(ThrownTrident instance, ProjectileDeflection projectileDeflection, Entity entity,
                                  EntityReference<Entity> entityReference, boolean deflectionByPlayer) {
        return true;
    }

    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void removeHitSlowDown(ThrownTrident instance, Vec3 speed) {
        // 미사용
    }
}
