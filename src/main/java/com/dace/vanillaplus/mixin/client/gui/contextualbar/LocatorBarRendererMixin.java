package com.dace.vanillaplus.mixin.client.gui.contextualbar;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPGameRules;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
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
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarRendererMixin implements VPMixin<LocatorBarRenderer> {
    @Unique
    private static final int PLAYER_HEAD_SIZE = 8;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    @Nullable
    private PlayerSkin getPlayerSkin(@NonNull Avatar avatar) {
        return switch (avatar) {
            case ClientMannequin target -> target.getSkin();
            case Player target -> {
                PlayerInfo playerInfo = Objects.requireNonNull(minecraft.player).connection.getPlayerInfo(target.getUUID());
                yield playerInfo == null ? null : playerInfo.getSkin();
            }
            default -> null;
        };
    }

    @Inject(method = "lambda$extractRenderState$1", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/waypoints/TrackedWaypoint$PitchDirection;NONE:Lnet/minecraft/world/waypoints/TrackedWaypoint$PitchDirection;",
            opcode = Opcodes.GETSTATIC))
    private void drawPlayerHead(Entity cameraEntity, Level level, PartialTickSupplier partialTickSupplier, GuiGraphicsExtractor graphics, int top,
                                TrackedWaypoint waypoint, CallbackInfo ci, @Local(name = "screenMiddle") int screenMiddle,
                                @Local(name = "dotPosition") int dotPosition,
                                @Local(name = "pitchDirection") TrackedWaypoint.PitchDirection pitchDirection) {
        if (!VPGameRules.ClientState.getInstance().isShowHeadOnLocatorBar()
                || !(waypoint.id().left().map(level::getEntity).orElse(null) instanceof Avatar avatar))
            return;

        PlayerSkin playerSkin = getPlayerSkin(avatar);
        if (playerSkin == null)
            return;

        int offset = pitchDirection == TrackedWaypoint.PitchDirection.DOWN ? -1 : 0;
        PlayerFaceExtractor.extractRenderState(graphics, playerSkin, screenMiddle + dotPosition + 1, top - 1 + offset, PLAYER_HEAD_SIZE);
    }
}
