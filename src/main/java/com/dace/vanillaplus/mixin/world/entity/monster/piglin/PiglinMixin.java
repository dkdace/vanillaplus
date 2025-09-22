package com.dace.vanillaplus.mixin.world.entity.monster.piglin;

import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.rebalance.modifier.DataModifiers;
import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Piglin.class)
public abstract class PiglinMixin extends MobMixin {
    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/piglin/Piglin;performCrossbowAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"),
            index = 1)
    private float modifyBulletSpeed(float speed, @Local(argsOnly = true) LivingEntity entity) {
        return ((EntityModifier.CrossbowAttackMobModifier) DataModifiers.get(entity.registryAccess(), DataModifiers.ENTITY_MODIFIER_MAP,
                EntityType.PIGLIN)).getShootingPower();
    }
}
