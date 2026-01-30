package com.dace.vanillaplus.mixin.server.level;

import com.dace.vanillaplus.mixin.world.entity.player.PlayerMixin;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends PlayerMixin<ServerPlayer> {
    @Inject(method = "die", at = @At("TAIL"))
    private void stopUsingItemOnDie(DamageSource damageSource, CallbackInfo ci) {
        stopUsingItem();
    }
}
