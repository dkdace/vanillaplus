package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.rebalance.modifier.EntityModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Pillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends MobMixin<EntityModifier.CrossbowAttackMobModifier> {
    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Pillager;performCrossbowAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"), index = 1)
    private float modifyBulletSpeed(float speed, @Local(argsOnly = true) LivingEntity entity) {
        return Objects.requireNonNull(dataModifier).getShootingPower();
    }
}
