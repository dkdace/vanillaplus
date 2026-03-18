package com.dace.vanillaplus.mixin.world.item.component;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttackRange.class)
public abstract class AttackRangeMixin implements VPMixin<AttackRange> {
    @Unique
    private static float getFinalAttackReach(float reach, Entity entity) {
        return entity instanceof LivingEntity livingEntity
                ? (float) (reach * livingEntity.getAttributeValue(VPAttributes.ATTACK_REACH_MULTIPLIER.getHolder().orElseThrow()))
                : reach;
    }

    @ModifyReturnValue(method = "effectiveMinRange", at = @At("RETURN"))
    private float modifyMinAttackReach(float reach, @Local(argsOnly = true) Entity entity) {
        return getFinalAttackReach(reach, entity);
    }

    @ModifyReturnValue(method = "effectiveMaxRange", at = @At("RETURN"))
    private float modifyMaxAttackReach(float reach, @Local(argsOnly = true) Entity entity) {
        return getFinalAttackReach(reach, entity);
    }

    @Definition(id = "d0", local = @Local(type = double.class, ordinal = 0))
    @Definition(id = "d1", local = @Local(type = double.class, ordinal = 1))
    @Expression("d1 < d0")
    @ModifyExpressionValue(method = "getClosesetHit", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyHitCondition(boolean condition, @Local(argsOnly = true) Entity entity, @Local(ordinal = 1) EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        return condition && (target.getRootVehicle() != entity.getRootVehicle() || target.canRiderInteract());
    }
}
