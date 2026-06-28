package com.dace.vanillaplus.mixin.server.level;

import com.dace.vanillaplus.mixin.world.entity.player.PlayerMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends PlayerMixin<ServerPlayer> {
    @Unique
    private float lastSentSaturation = -99999999;

    @Inject(method = "die", at = @At("TAIL"))
    private void stopUsingItemOnDie(DamageSource source, CallbackInfo ci) {
        stopUsingItem();
    }

    @Definition(id = "lastFoodSaturationZero", field = "Lnet/minecraft/server/level/ServerPlayer;lastFoodSaturationZero:Z")
    @Expression("? != this.lastFoodSaturationZero")
    @ModifyExpressionValue(method = "doTick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyHealthPacketCondition(boolean original) {
        return lastSentSaturation != foodData.getSaturationLevel();
    }

    @Redirect(method = "doTick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;lastFoodSaturationZero:Z",
            opcode = Opcodes.PUTFIELD))
    private void setLastSentSaturation(ServerPlayer instance, boolean value) {
        lastSentSaturation = foodData.getSaturationLevel();
    }
}
