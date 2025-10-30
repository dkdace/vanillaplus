package com.dace.vanillaplus.mixin.world.entity.monster;

import com.dace.vanillaplus.data.RaiderEffect;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.util.ReflectionUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.util.Objects;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends AbstractIllagerMixin<Pillager, EntityModifier.CrossbowAttackMobModifier> {
    @Inject(method = "registerGoals", at = @At(value = "NEW",
            target = "(Lnet/minecraft/world/entity/PathfinderMob;Ljava/lang/Class;FDD)Lnet/minecraft/world/entity/ai/goal/AvoidEntityGoal;"))
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
    private void setCanOpenDoors(EntityType<? extends Pillager> entityType, Level level, CallbackInfo ci) {
        getNavigation().setCanOpenDoors(true);
    }

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Pillager;performCrossbowAttack(Lnet/minecraft/world/entity/LivingEntity;F)V"), index = 1)
    private float modifyBulletSpeed(float speed, @Local(argsOnly = true) LivingEntity entity) {
        return Objects.requireNonNull(dataModifier).getShootingPower();
    }

    @Overwrite
    public void applyRaidBuffs(ServerLevel serverLevel, int wave, boolean ignored) {
        RaiderEffect.PillagerEffect pillagerEffect = RaiderEffect.fromEntityType(getType());
        pillagerEffect.getEnchantItemInfos().forEach(enchantItemEffect -> enchantItemEffect.applyEnchantment(getThis()));
    }

    @Override
    public ItemStack modifyProjectileItem(ItemStack itemStack) {
        return ((RaiderEffect.PillagerEffect) RaiderEffect.fromEntityType(getType())).getTippedArrowInfo()
                .applyArrowPotionEffect(getThis(), itemStack);
    }
}
