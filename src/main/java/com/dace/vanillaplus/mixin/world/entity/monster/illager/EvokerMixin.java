package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.dace.vanillaplus.data.ReloadableDataManager;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.dace.vanillaplus.world.entity.raid.RaiderEffect;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Evoker.class)
public abstract class EvokerMixin extends AbstractIllagerMixin<Evoker, EntityModifier.LivingEntityModifier> {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addOpenDoorGoal(CallbackInfo ci) {
        targetSelector.addGoal(1, getThis().new RaiderOpenDoorGoal(getThis()));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCanOpenDoors(EntityType<? extends Evoker> entityType, Level level, CallbackInfo ci) {
        getNavigation().setCanOpenDoors(true);
    }

    @Overwrite
    public void applyRaidBuffs(ServerLevel serverLevel, int wave, boolean ignored) {
        getRaiderEffect(RaiderEffect.EvokerEffect.class).ifPresent(evokerEffect ->
                evokerEffect.getEquipItemInfos().forEach(itemInfo -> itemInfo.equipItem(getThis())));
    }

    @Mixin(targets = "net.minecraft.world.entity.monster.illager.Evoker$EvokerSummonSpellGoal")
    public abstract static class EvokerSummonSpellGoalMixin {
        @Inject(method = "performSpellCasting", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/monster/Vex;setBoundOrigin(Lnet/minecraft/core/BlockPos;)V"))
        private void applyRaidBuffsToVex(CallbackInfo ci, @Local Vex vex) {
            ReloadableDataManager.RAIDER_EFFECT.get(EntityType.EVOKER, RaiderEffect.EvokerEffect.class).ifPresent(evokerEffect ->
                    evokerEffect.getVexMobEffectInfos().forEach(enchantItemInfo -> enchantItemInfo.applyMobEffect(vex)));
        }
    }
}
