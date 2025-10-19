package com.dace.vanillaplus.mixin.world.entity.monster;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.monster.Ravager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Ravager.class)
public abstract class RavagerMixin {
    @ModifyReturnValue(method = "lambda$registerGoals$3", at = @At("RETURN"))
    private static boolean modifyAttackGoal(boolean original) {
        return true;
    }
}
