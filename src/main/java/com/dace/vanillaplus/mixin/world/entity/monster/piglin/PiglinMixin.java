package com.dace.vanillaplus.mixin.world.entity.monster.piglin;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import com.dace.vanillaplus.registryobject.EntityModifierInterfaces;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Piglin.class)
public abstract class PiglinMixin extends MonsterMixin<Piglin, EntityModifier.LivingEntityModifier> {
    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/piglin/Piglin;performCrossbowAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"),
            index = 1)
    private float modifyBulletVelocity(float velocity) {
        return getDataModifier()
                .flatMap(livingEntityModifier -> livingEntityModifier.get(EntityModifierInterfaces.CROSSBOW_ATTACK_MOB)
                        .map(EntityModifier.CrossbowAttackMobInfo::getShootingPower))
                .orElse(velocity);
    }
}
