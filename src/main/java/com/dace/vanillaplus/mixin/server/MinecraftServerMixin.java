package com.dace.vanillaplus.mixin.server;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.packet.ShowHeadOnLocatorBarPacketHandler;
import com.dace.vanillaplus.registryobject.VPGameRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.gamerules.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements VPMixin<MinecraftServer> {
    @Shadow
    public abstract PlayerList getPlayerList();

    @Inject(method = "onGameRuleChanged", at = @At("TAIL"))
    private <T> void updateGameRules(GameRule<T> gameRule, T value, CallbackInfo ci) {
        if (gameRule == VPGameRules.SHOW_HEAD_ON_LOCATOR_BAR.get())
            getPlayerList().getPlayers().forEach(serverPlayer ->
                    NetworkManager.sendToPlayer(new ShowHeadOnLocatorBarPacketHandler((boolean) value), serverPlayer));
    }
}
