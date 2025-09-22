package com.dace.vanillaplus.mixin.world.entity.ai.behavior;

import com.dace.vanillaplus.rebalance.modifier.DataModifiers;
import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BehaviorUtils.class)
public abstract class BehaviorUtilsMixin {
    @Redirect(method = "isWithinAttackRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getDefaultProjectileRange()I"))
    private static int modifyAttackRange(ProjectileWeaponItem instance, @Local(argsOnly = true) Mob mob) {
        if (!(mob instanceof CrossbowAttackMob))
            return instance.getDefaultProjectileRange();

        return ((EntityModifier.CrossbowAttackMobModifier) DataModifiers.get(mob.registryAccess(), DataModifiers.ENTITY_MODIFIER_MAP, mob.getType()))
                .getShootingRange();
    }
}
