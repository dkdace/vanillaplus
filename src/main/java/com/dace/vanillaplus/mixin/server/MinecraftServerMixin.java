package com.dace.vanillaplus.mixin.server;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.client.ShowHeadOnLocatorBarPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements VPMixin<MinecraftServer> {
    @Inject(method = "onGameRuleChanged", at = @At("TAIL"))
    private <T> void updateGameRules(GameRule<T> rule, T value, CallbackInfo ci) {
        if (rule == VPGameRules.SHOW_HEAD_ON_LOCATOR_BAR.get())
            NetworkManager.broadcast(new ShowHeadOnLocatorBarPacket((boolean) value));
    }
}
