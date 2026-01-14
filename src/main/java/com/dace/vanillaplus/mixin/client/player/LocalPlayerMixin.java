package com.dace.vanillaplus.mixin.client.player;

import com.dace.vanillaplus.mixin.world.entity.player.PlayerMixin;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends PlayerMixin<LocalPlayer> {
    @Shadow
    public abstract boolean isMovingSlowly();

    @ModifyReturnValue(method = "shouldStopRunSprinting", at = @At("RETURN"))
    private boolean modifyStopSprintConditions(boolean original) {
        return original || isMovingSlowly();
    }

    @ModifyExpressionValue(method = "raycastHitResult", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;entityInteractionRange()D"))
    private double modifyFallbackAttackReach(double reach) {
        return reach * getAttributeValue(VPAttributes.ATTACK_REACH_MULTIPLIER.getHolder().orElseThrow());
    }
}
