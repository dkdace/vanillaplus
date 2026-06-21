package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.dace.vanillaplus.mixin.world.entity.raid.RaiderMixin;
import com.dace.vanillaplus.world.entity.raid.RaiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractIllager.class)
public abstract class AbstractIllagerMixin<T extends AbstractIllager> extends RaiderMixin<T> {
    @WrapOperation(method = "canAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isBaby()Z"))
    private boolean redirectCanAttackBaby(LivingEntity instance, Operation<Boolean> original) {
        return !RaiderConfig.get(getThis()).attackBabyVillagers() && original.call(instance);
    }
}
