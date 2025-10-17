package com.dace.vanillaplus.mixin.world.entity.player;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.extension.VPPlayer;
import com.dace.vanillaplus.mixin.world.entity.LivingEntityMixin;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseCooldown;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin<Player, EntityModifier.LivingEntityModifier> implements VPPlayer<Player> {
    @Shadow
    @Final
    private Abilities abilities;
    @Shadow
    @Final
    private ItemCooldowns cooldowns;
    @Unique
    private boolean isProneKeyDown = false;

    @Override
    public void setProneKeyDown(boolean isProneKeyDown) {
        this.isProneKeyDown = isProneKeyDown;
    }

    @ModifyReturnValue(method = "getDesiredPose", at = @At(value = "RETURN", ordinal = 4))
    private Pose modifyDesiredPose(Pose pose) {
        return isProneKeyDown && !abilities.flying && onGround() ? Pose.SWIMMING : pose;
    }

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
}
