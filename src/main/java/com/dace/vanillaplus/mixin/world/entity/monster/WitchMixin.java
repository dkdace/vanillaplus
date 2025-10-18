package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Witch.class)
public abstract class WitchMixin extends MobMixin<Witch, EntityModifier.LivingEntityModifier> {
    @Unique
    private NearestAttackableWitchTargetGoal<IronGolem> attackIronGolemGoal;
    @Unique
    private NearestAttackableWitchTargetGoal<AbstractVillager> attackVillagersGoal;

    @Inject(method = "registerGoals", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/monster/Witch;attackPlayersGoal:Lnet/minecraft/world/entity/ai/goal/target/NearestAttackableWitchTargetGoal;",
            ordinal = 1))
    private void addVillagerAttackGoal(CallbackInfo ci) {
        attackVillagersGoal = new NearestAttackableWitchTargetGoal<>(getThis(), AbstractVillager.class, 10, true, false,
                null);
        attackIronGolemGoal = new NearestAttackableWitchTargetGoal<>(getThis(), IronGolem.class, 10, true, false,
                null);
        targetSelector.addGoal(3, attackVillagersGoal);
        targetSelector.addGoal(3, attackIronGolemGoal);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/target/NearestAttackableWitchTargetGoal;setCanAttack(Z)V", ordinal = 0))
    private void setAttackGoalCanAttackTrue(CallbackInfo ci) {
        attackVillagersGoal.setCanAttack(true);
        attackIronGolemGoal.setCanAttack(true);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/target/NearestAttackableWitchTargetGoal;setCanAttack(Z)V", ordinal = 1))
    private void setAttackGoalCanAttackFalse(CallbackInfo ci) {
        attackVillagersGoal.setCanAttack(false);
        attackIronGolemGoal.setCanAttack(false);
    }
}
