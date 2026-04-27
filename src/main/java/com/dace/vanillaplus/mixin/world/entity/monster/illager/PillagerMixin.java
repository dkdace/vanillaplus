package com.dace.vanillaplus.mixin.world.entity.monster.illager;

import com.dace.vanillaplus.data.registryobject.EntityModifierInterfaces;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.dace.vanillaplus.world.entity.raid.RaiderEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends AbstractIllagerMixin<Pillager, EntityModifier.LivingEntityModifier> {
    @Override
    public ItemStack getProjectile(ItemStack heldWeapon) {
        ItemStack itemStack = super.getProjectile(heldWeapon);

        if (level().isClientSide())
            return itemStack;

        return getRaiderEffect(RaiderEffect.PillagerEffect.class)
                .map(pillagerEffect -> pillagerEffect.getTippedArrowInfo().applyArrowPotionEffect(getThis(), itemStack))
                .orElse(itemStack);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addOpenDoorGoal(CallbackInfo ci) {
        targetSelector.addGoal(1, getThis().new RaiderOpenDoorGoal(getThis()));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setCanOpenDoors(EntityType<? extends Pillager> type, Level level, CallbackInfo ci) {
        getNavigation().setCanOpenDoors(true);
    }

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/illager/Pillager;performCrossbowAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"),
            index = 1)
    private float modifyBulletVelocity(float crossbowPower) {
        return getDataModifier()
                .flatMap(livingEntityModifier -> livingEntityModifier.get(EntityModifierInterfaces.CROSSBOW_ATTACK_MOB)
                        .map(EntityModifier.CrossbowAttackMobInfo::getShootingPower))
                .orElse(crossbowPower);
    }

    @Overwrite
    public void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain) {
        getRaiderEffect(RaiderEffect.PillagerEffect.class).ifPresent(pillagerEffect ->
                pillagerEffect.getEnchantItemInfos().forEach(enchantItemEffect -> enchantItemEffect.applyEnchantment(getThis())));
    }
}
