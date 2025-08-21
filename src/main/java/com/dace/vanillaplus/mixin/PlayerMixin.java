package com.dace.vanillaplus.mixin;

import com.dace.vanillaplus.custom.CustomPlayer;
import com.dace.vanillaplus.rebalance.Rebalance;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin implements CustomPlayer {
    @Shadow
    @Final
    private Abilities abilities;
    @Shadow
    @Final
    private ItemCooldowns cooldowns;
    @Unique
    private boolean vp$isProneKeyDown = false;

    @Override
    public void vp$setProneKeyDown(boolean isProneKeyDown) {
        vp$isProneKeyDown = isProneKeyDown;
    }

    @ModifyReturnValue(method = "getDesiredPose", at = @At(value = "RETURN", ordinal = 4))
    private Pose modifyDesiredPose(Pose pose) {
        return vp$isProneKeyDown && !abilities.flying && onGround() ? Pose.SWIMMING : pose;
    }

    @Override
    protected boolean canUseTotem(boolean canUse, ItemStack itemStack) {
        return canUse && !cooldowns.isOnCooldown(itemStack);
    }

    @Override
    protected void onUseTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, ItemStack itemStack) {
        cooldowns.addCooldown(itemStack, Rebalance.Totem.COOLDOWN_SECONDS * 20);
    }
}
