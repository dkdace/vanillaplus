package com.dace.vanillaplus.mixin.world.entity.ai.behavior;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import com.dace.vanillaplus.registryobject.EntityModifierInterfaces;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BehaviorUtils.class)
public abstract class BehaviorUtilsMixin implements VPMixin<BehaviorUtils> {
    @ModifyExpressionValue(method = "isWithinAttackRange", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getDefaultProjectileRange()I"))
    private static int modifyAttackRange(int attackRange, @Local(argsOnly = true) Mob mob) {
        return VPEntity.cast(mob).getDataModifier()
                .flatMap(entityModifier -> entityModifier.get(EntityModifierInterfaces.CROSSBOW_ATTACK_MOB)
                        .map(EntityModifier.CrossbowAttackMobInfo::getShootingRange))
                .orElse(attackRange);
    }
}
