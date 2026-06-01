package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.dace.vanillaplus.world.entity.raid.RaiderConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Illusioner.class)
public abstract class IllusionerMixin extends AbstractIllagerMixin<Illusioner> {
    @Inject(method = "applyRaidBuffs", at = @At("TAIL"))
    private void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain, CallbackInfo ci) {
        applyCustomRaidBuffs();
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addOpenDoorGoal(CallbackInfo ci) {
        if (RaiderConfig.get(getThis()).alwaysOpenDoors())
            targetSelector.addGoal(6, new OpenDoorGoal(getThis(), false));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCanOpenDoors(EntityType<? extends Illusioner> type, Level level, CallbackInfo ci) {
        if (RaiderConfig.get(getThis()).alwaysOpenDoors())
            getNavigation().setCanOpenDoors(true);
    }
}
