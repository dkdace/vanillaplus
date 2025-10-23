package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Ravager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Ravager.class)
public abstract class RavagerMixin extends MonsterMixin<Ravager, EntityModifier.LivingEntityModifier> {
    @ModifyReturnValue(method = "lambda$registerGoals$3", at = @At("RETURN"))
    private static boolean modifyAttackGoal(boolean original) {
        return true;
    }

    @Overwrite
    public void applyRaidBuffs(ServerLevel serverLevel, int wave, boolean ignored) {
        RaiderEffect.RavagerEffect ravagerEffect = RaiderEffect.fromEntityType(getType());
        ravagerEffect.getMobEffectInfos().forEach(mobEffectEffect -> mobEffectEffect.applyMobEffect(getThis()));
    }
}
