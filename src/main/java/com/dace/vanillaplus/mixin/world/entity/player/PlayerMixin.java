package com.dace.vanillaplus.mixin.world.entity.player;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.world.entity.player.VPPlayer;
import com.dace.vanillaplus.mixin.world.entity.LivingEntityMixin;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin<T extends Player> extends LivingEntityMixin<T, EntityModifier.LivingEntityModifier> implements VPPlayer<T> {
    @Unique
    protected boolean isProneKeyDown = false;
    @Shadow
    @Final
    private Abilities abilities;
    @Shadow
    @Final
    private ItemCooldowns cooldowns;

    @Override
    protected boolean canUseTotem(boolean canUse, ItemStack itemStack) {
        return canUse && !cooldowns.isOnCooldown(itemStack);
    }

    @Override
    protected void onUseTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, ItemStack itemStack) {
        UseCooldown useCooldown = itemStack.get(DataComponents.USE_COOLDOWN);
        if (useCooldown != null)
            useCooldown.apply(itemStack, getThis());
    }

    @Override
    public void setProneKeyDown(boolean isProneKeyDown) {
        this.isProneKeyDown = isProneKeyDown;
    }

    @ModifyReturnValue(method = "getDesiredPose", at = @At(value = "RETURN", ordinal = 4))
    private Pose modifyDesiredPose(Pose pose) {
        return isProneKeyDown && !abilities.flying && onGround() ? Pose.SWIMMING : pose;
    }

    @ModifyArg(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private float modifyFoodExhaustion(float exhaustion) {
        return (float) (exhaustion * getAttributeValue(VPAttributes.FOOD_EXHAUSTION_MULTIPLIER.getHolder().orElseThrow()));
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"))
    private void removeSprintCancelOnAttack(Player player, boolean isSprinting) {
        // 미사용
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<Entity> modifyTouchEntities(List<Entity> entities, @Local AABB aabb) {
        double pickupRange = (getAttributeValue(VPAttributes.ITEM_PICKUP_RANGE.getHolder().orElseThrow()) - 1) * 0.5;

        entities.addAll(level().getEntities(getThis(),
                aabb.inflate(aabb.getXsize() * pickupRange, aabb.getYsize() * pickupRange, aabb.getZsize() * pickupRange),
                entity -> entity instanceof ItemEntity || entity instanceof AbstractArrow || entity instanceof ExperienceOrb));

        return entities;
    }
}
