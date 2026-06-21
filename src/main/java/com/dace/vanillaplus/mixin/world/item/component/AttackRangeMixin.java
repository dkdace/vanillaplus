package com.dace.vanillaplus.mixin.world.item.component;

import com.dace.vanillaplus.data.registryobject.VPAttributes;
import com.dace.vanillaplus.extension.VPMixin;
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
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttackRange.class)
public abstract class AttackRangeMixin implements VPMixin<AttackRange> {
    @ModifyReturnValue(method = {"effectiveMinRange", "effectiveMaxRange"}, at = @At("RETURN"))
    private float modifyAttackReach(float reach, @Local(argsOnly = true) Entity entity) {
        return entity instanceof LivingEntity livingEntity
                ? (float) (reach * livingEntity.getAttributeValue(VPAttributes.ATTACK_REACH_MULTIPLIER.getHolder().orElseThrow()))
                : reach;
    }

    @Definition(id = "closestDistance", local = @Local(type = double.class, name = "closestDistance"))
    @Definition(id = "distance", local = @Local(type = double.class, name = "distance"))
    @Expression("distance < closestDistance")
    @ModifyExpressionValue(method = "getClosesetHit", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyHitCondition(boolean condition, @Local(argsOnly = true) Entity attacker, @Local(name = "target") EntityHitResult target) {
        Entity entity = target.getEntity();
        return condition && (entity.getRootVehicle() != attacker.getRootVehicle() || entity.canRiderInteract());
    }
}
