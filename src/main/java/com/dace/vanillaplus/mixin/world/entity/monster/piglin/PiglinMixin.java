package com.dace.vanillaplus.mixin.world.entity.monster.piglin;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Piglin.class)
public abstract class PiglinMixin extends MonsterMixin<Piglin> {
    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/piglin/Piglin;performCrossbowAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"),
            index = 1)
    private float modifyBulletVelocity(float crossbowPower) {
        return getConfigComponents().get(EntityConfigComponentTypes.CROSSBOW_MOB).shootingPower().orElse(crossbowPower);
    }
}
