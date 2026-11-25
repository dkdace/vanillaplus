package com.dace.vanillaplus.mixin.world.entity.monster.zombie;

import com.dace.vanillaplus.mixin.world.entity.monster.MonsterMixin;
import com.dace.vanillaplus.world.entity.ai.ShieldUseGoal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends MonsterMixin<Zombie> {
    @Inject(method = "addBehaviourGoals", at = @At("TAIL"))
    private void addShieldUseGoal(CallbackInfo ci) {
        goalSelector.addGoal(2, new ShieldUseGoal<>(getThis(), 16, 30, 20, 40));
    }
}
