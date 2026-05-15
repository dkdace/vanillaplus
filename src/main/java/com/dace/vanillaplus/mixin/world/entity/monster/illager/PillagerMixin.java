package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends AbstractIllagerMixin<Pillager> {
    @Inject(method = "applyRaidBuffs", at = @At("TAIL"))
    private void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain, CallbackInfo ci) {
        applyCustomRaidBuffs();
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addOpenDoorGoal(CallbackInfo ci) {
        if (getRaiderConfig().alwaysOpenDoors())
            targetSelector.addGoal(3, new OpenDoorGoal(getThis(), false));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCanOpenDoors(EntityType<? extends Pillager> type, Level level, CallbackInfo ci) {
        if (getRaiderConfig().alwaysOpenDoors())
            getNavigation().setCanOpenDoors(true);
    }

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/illager/Pillager;performCrossbowAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"),
            index = 1)
    private float modifyBulletVelocity(float crossbowPower) {
        return getConfigComponents().get(EntityConfigComponentTypes.CROSSBOW_MOB).shootingPower().orElse(crossbowPower);
    }
}
