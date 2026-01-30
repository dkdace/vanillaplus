package com.dace.vanillaplus.mixin.world.entity.animal.wolf;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Wolf.class)
public abstract class WolfMixin extends MobMixin<Wolf, EntityModifier.LivingEntityModifier> {
    @Redirect(method = "canMate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;isTame()Z"))
    public boolean redirectTameCondition(Wolf wolf) {
        return !wolf.isAngry();
    }
}
