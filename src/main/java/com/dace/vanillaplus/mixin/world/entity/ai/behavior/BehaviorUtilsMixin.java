package com.dace.vanillaplus.mixin.world.entity.ai.behavior;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BehaviorUtils.class)
public abstract class BehaviorUtilsMixin implements VPMixin<BehaviorUtils> {
    @Redirect(method = "isWithinAttackRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getDefaultProjectileRange()I"))
    private static int modifyAttackRange(ProjectileWeaponItem instance, @Local(argsOnly = true) Mob mob) {
        if (!(mob instanceof CrossbowAttackMob))
            return instance.getDefaultProjectileRange();

        return ((EntityModifier.CrossbowAttackMobModifier) VPRegistries.getValueOrThrow(EntityModifier.fromEntityType(mob.getType())))
                .getShootingRange();
    }
}
