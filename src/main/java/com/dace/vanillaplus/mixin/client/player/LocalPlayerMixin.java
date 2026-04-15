package com.dace.vanillaplus.mixin.client.player;

import com.dace.vanillaplus.mixin.world.entity.player.PlayerMixin;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends PlayerMixin<LocalPlayer> {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    public abstract boolean isMovingSlowly();

    @ModifyReturnValue(method = "shouldStopRunSprinting", at = @At("RETURN"))
    private boolean modifyStopSprintCondition(boolean condition) {
        return condition || isMovingSlowly();
    }

    @ModifyExpressionValue(method = "raycastHitResult", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;entityInteractionRange()D"))
    private double modifyFallbackAttackReach(double reach) {
        return reach * getAttributeValue(VPAttributes.ATTACK_REACH_MULTIPLIER.getHolder().orElseThrow());
    }

    @Inject(method = "swing", at = @At("HEAD"))
    public void resetAttackStrengthOnSwing(InteractionHand hand, CallbackInfo ci) {
        attackStrengthTicker = 0;
    }

    @Inject(method = "drop", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;removeFromSelected(Z)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER))
    private void stopDestroyingBlockOnDrop(boolean all, CallbackInfoReturnable<Boolean> cir) {
        MultiPlayerGameMode gameMode = minecraft.gameMode;
        if (gameMode != null)
            gameMode.stopDestroyBlock();
    }
}
