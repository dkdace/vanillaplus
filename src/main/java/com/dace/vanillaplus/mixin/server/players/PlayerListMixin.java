package com.dace.vanillaplus.mixin.server.players;

import com.dace.vanillaplus.data.registryobject.VPGameRules;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.client.ShowHeadOnLocatorBarPacket;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin implements VPMixin<PlayerList> {
    @Inject(method = "placeNewPlayer", at = @At(value = "NEW",
            target = "(Lnet/minecraft/world/Difficulty;Z)Lnet/minecraft/network/protocol/game/ClientboundChangeDifficultyPacket;"))
    private void sendGameRulesOnJoin(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci,
                                     @Local(name = "gameRules") GameRules gameRules) {
        NetworkManager.sendToPlayer(new ShowHeadOnLocatorBarPacket(gameRules.get(VPGameRules.SHOW_HEAD_ON_LOCATOR_BAR.get())), player);
    }
}
