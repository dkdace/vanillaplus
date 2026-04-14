package com.dace.vanillaplus.mixin.client.gui.contextualbar;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPGameRules;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.PartialTickSupplier;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarRendererMixin implements VPMixin<LocatorBarRenderer> {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "lambda$extractRenderState$1", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/waypoints/TrackedWaypoint$PitchDirection;NONE:Lnet/minecraft/world/waypoints/TrackedWaypoint$PitchDirection;",
            opcode = Opcodes.GETSTATIC))
    private void drawPlayerHead(Entity entity, Level level, PartialTickSupplier partialTickSupplier, GuiGraphicsExtractor guiGraphicsExtractor,
                                int startY, TrackedWaypoint trackedWaypoint, CallbackInfo ci, @Local(ordinal = 1) int startX,
                                @Local(ordinal = 3) int x, @Local TrackedWaypoint.PitchDirection pitchDirection) {
        if (!VPGameRules.ClientState.getInstance().isShowHeadOnLocatorBar()
                || !(trackedWaypoint.id().left().map(level::getEntity).orElse(null) instanceof Avatar avatar))
            return;

        PlayerSkin playerSkin = null;

        switch (avatar) {
            case ClientMannequin target -> playerSkin = target.getSkin();
            case Player target -> {
                PlayerInfo playerInfo = Objects.requireNonNull(minecraft.player).connection.getPlayerInfo(target.getUUID());

                if (playerInfo != null)
                    playerSkin = playerInfo.getSkin();
            }
            default -> {
                // 미사용
            }
        }

        if (playerSkin == null)
            return;

        int offset = pitchDirection == TrackedWaypoint.PitchDirection.DOWN ? -1 : 0;
        PlayerFaceExtractor.extractRenderState(guiGraphicsExtractor, playerSkin, startX + x + 1, startY - 1 + offset, 8);
    }
}
