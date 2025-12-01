package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.raid.RaiderMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Witch.class)
public abstract class WitchMixin extends RaiderMixin<Witch, EntityModifier.LivingEntityModifier> {
    @Unique
    private NearestAttackableWitchTargetGoal<IronGolem> attackIronGolemGoal;
    @Unique
    private NearestAttackableWitchTargetGoal<AbstractVillager> attackVillagersGoal;

    @Inject(method = "registerGoals", at = @At("TAIL"))
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

    @Redirect(method = "registerGoals", at = @At(value = "NEW",
            target = "(Lnet/minecraft/world/entity/monster/RangedAttackMob;DIF)Lnet/minecraft/world/entity/ai/goal/RangedAttackGoal;"))
    private RangedAttackGoal modifyAttackCooldown(RangedAttackMob mob, double speedModifier, int attackInterval, float attackRadius) {
        int attackIntervalMin = ((RaiderEffect.WitchEffect) RaiderEffect.fromEntityType(getType())).getModifyPotionCooldownInfos().stream()
                .mapToInt(modifyValueInfo -> modifyValueInfo.modifyValue(getThis(), attackInterval))
                .min()
                .orElse(attackInterval);

        return new RangedAttackGoal(mob, speedModifier, attackIntervalMin, attackInterval, attackRadius);
    }

    @ModifyExpressionValue(method = "performRangedAttack", at = @At(value = "CONSTANT", args = "floatValue=4.0"))
    private float modifySupportHealthCondition(float health) {
        return 8;
    }

    @ModifyExpressionValue(method = "performRangedAttack", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/item/alchemy/Potions;REGENERATION:Lnet/minecraft/core/Holder;", opcode = Opcodes.GETSTATIC))
    private Holder<Potion> modifySupportPotion(Holder<Potion> potionHolder) {
        return random.nextBoolean() ? Potions.SWIFTNESS : potionHolder;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack upgradePotionSelf(ItemStack itemStack) {
        RaiderEffect.WitchEffect witchEffect = RaiderEffect.fromEntityType(getType());
        itemStack = witchEffect.getUpgradePotionForSelfInfo().upgradePotion(getThis(), itemStack);

        return witchEffect.getReplacePotionForSelfInfo().replacePotion(getThis(), itemStack);
    }

    @ModifyExpressionValue(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack upgradePotionThrow(ItemStack itemStack, @Local(argsOnly = true) LivingEntity target) {
        RaiderEffect.WitchEffect witchEffect = RaiderEffect.fromEntityType(getType());

        return (target instanceof Raider ? witchEffect.getUpgradePotionForSupportInfo() : witchEffect.getUpgradePotionForAttackInfo())
                .upgradePotion(getThis(), itemStack);
    }
}
