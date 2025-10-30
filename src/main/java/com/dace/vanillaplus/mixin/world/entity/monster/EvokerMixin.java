package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.util.ReflectionUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;

@Mixin(Evoker.class)
public abstract class EvokerMixin extends AbstractIllagerMixin<Evoker, EntityModifier.LivingEntityModifier> {
    @Inject(method = "registerGoals", at = @At(value = "NEW",
            target = "(Lnet/minecraft/world/entity/PathfinderMob;Ljava/lang/Class;FDD)Lnet/minecraft/world/entity/ai/goal/AvoidEntityGoal;",
            ordinal = 0))
    private void addOpenDoorGoal(CallbackInfo ci) {
        try {
            Class<?> raiderOpenDoorGoalClass = ReflectionUtil.getClass("net.minecraft.world.entity.monster.AbstractIllager$RaiderOpenDoorGoal");
            Constructor<?> raidOpenDoorGoalConstructor = ReflectionUtil.getConstructor(raiderOpenDoorGoalClass, AbstractIllager.class, Raider.class);

            targetSelector.addGoal(1, (Goal) raidOpenDoorGoalConstructor.newInstance(this, this));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCanOpenDoors(EntityType<? extends Evoker> entityType, Level level, CallbackInfo ci) {
        getNavigation().setCanOpenDoors(true);
    }

    @Overwrite
    public void applyRaidBuffs(ServerLevel serverLevel, int wave, boolean ignored) {
        RaiderEffect.EvokerEffect evokerEffect = RaiderEffect.fromEntityType(getType());
        evokerEffect.getEquipItemInfos().forEach(itemInfo -> itemInfo.equipItem(getThis()));
    }


    @Mixin(targets = "net.minecraft.world.entity.monster.Evoker$EvokerSummonSpellGoal")
    public abstract static class EvokerSummonSpellGoal {
        @Inject(method = "performSpellCasting", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/monster/Vex;setBoundOrigin(Lnet/minecraft/core/BlockPos;)V"))
        private void applyRaidBuffsToVex(CallbackInfo ci, @Local Vex vex) {
            RaiderEffect.EvokerEffect evokerEffect = RaiderEffect.fromEntityType(EntityType.EVOKER);
            evokerEffect.getVexMobEffectInfos().forEach(enchantItemInfo -> enchantItemInfo.applyMobEffect(vex));
        }
    }
}
