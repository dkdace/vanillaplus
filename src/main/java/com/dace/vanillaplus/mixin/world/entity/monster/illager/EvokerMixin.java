package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.dace.vanillaplus.extension.world.entity.raid.VPRaider;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Evoker.class)
public abstract class EvokerMixin extends AbstractIllagerMixin<Evoker> {
    @Override
    @Nullable
    public EquipmentSlot resolveSlot(@NonNull ItemStack toEquip, @NonNull List<EquipmentSlot> alreadyInsertedIntoSlots) {
        if (!getRaiderConfig().useDataDrivenRaidEquipment())
            return super.resolveSlot(toEquip, alreadyInsertedIntoSlots);

        if (!toEquip.isEmpty()) {
            Equippable equippable = toEquip.get(DataComponents.EQUIPPABLE);

            if (equippable != null) {
                EquipmentSlot slot = equippable.slot();
                if (!alreadyInsertedIntoSlots.contains(slot))
                    return slot;
            } else if (!alreadyInsertedIntoSlots.contains(EquipmentSlot.MAINHAND))
                return EquipmentSlot.MAINHAND;
            else if (!alreadyInsertedIntoSlots.contains(EquipmentSlot.OFFHAND))
                return EquipmentSlot.OFFHAND;
        }

        return null;
    }

    @Inject(method = "applyRaidBuffs", at = @At("TAIL"))
    private void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain, CallbackInfo ci) {
        applyCustomRaidBuffs();
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addOpenDoorGoal(CallbackInfo ci) {
        if (getRaiderConfig().alwaysOpenDoors())
            targetSelector.addGoal(7, new OpenDoorGoal(getThis(), false));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCanOpenDoors(EntityType<? extends Evoker> type, Level level, CallbackInfo ci) {
        if (getRaiderConfig().alwaysOpenDoors())
            getNavigation().setCanOpenDoors(true);
    }

    @Mixin(targets = "net.minecraft.world.entity.monster.illager.Evoker$EvokerSummonSpellGoal")
    public abstract static class EvokerSummonSpellGoalMixin {
        @Shadow
        @Final
        Evoker this$0;

        @Inject(method = "performSpellCasting", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/entity/monster/Vex;setBoundOrigin(Lnet/minecraft/core/BlockPos;)V"))
        private void applyRaidBuffsToVex(CallbackInfo ci, @Local(name = "vex") Vex vex) {
            VPRaider.cast(this$0).getRaiderConfig().raidSummonedMobEffects().apply(this$0, vex);
        }
    }
}
