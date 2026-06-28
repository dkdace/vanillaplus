package com.dace.vanillaplus.mixin.client.gui;

import com.dace.vanillaplus.extension.client.gui.VPGui;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin implements VPGui {
    @Unique
    private static final Identifier CROSSHAIR_AIR_SPRITE = IdentifierUtil.fromPath("hud/crosshair_air");
    @Unique
    private static final Identifier[] CROSSHAIR_HITMARKER_SPRITES = {
            IdentifierUtil.fromPath("hud/crosshair_hitmarker_0"),
            IdentifierUtil.fromPath("hud/crosshair_hitmarker_1"),
            IdentifierUtil.fromPath("hud/crosshair_hitmarker_2"),
            IdentifierUtil.fromPath("hud/crosshair_hitmarker_3")
    };
    @Unique
    private static final int RECENT_DAMAGE_DURATION = 12;
    @Unique
    private static final int[] CROSSHAIR_HITMARKER_DAMAGES = {5, 10, 15, Integer.MAX_VALUE};
    @Unique
    private static final int CROSSHAIR_HITMARKER_SIZE = 31;
    @Unique
    private static final int CROSSHAIR_HITMARKER_COLOR = ARGB.color(204, 255, 255, 255);
    @Unique
    private static final int CROSSHAIR_HITMARKER_COLOR_KILLED = ARGB.color(255, 255, 0, 0);

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private ItemStack lastToolHighlight;
    @Unique
    private int recentDamageTick = 0;
    @Unique
    private float recentDamage = 0;
    @Unique
    private Identifier hitmarkerSprite;
    @Unique
    private int hitmarkerColor = 0;

    @Override
    public void updateRecentDamage(float damage, boolean isKilled) {
        if (damage <= 0)
            return;

        recentDamage = recentDamage * ((float) recentDamageTick / RECENT_DAMAGE_DURATION) + damage;
        recentDamageTick = RECENT_DAMAGE_DURATION;
        hitmarkerColor = isKilled ? CROSSHAIR_HITMARKER_COLOR_KILLED : CROSSHAIR_HITMARKER_COLOR;

        for (int i = 0; i < CROSSHAIR_HITMARKER_DAMAGES.length; i++)
            if (recentDamage < CROSSHAIR_HITMARKER_DAMAGES[i]) {
                hitmarkerSprite = CROSSHAIR_HITMARKER_SPRITES[i];
                break;
            }
    }

    @Inject(method = "renderSelectedItemName", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
    private void applyEnchantedItemNameStyle(GuiGraphicsExtractor graphics, int yShift, CallbackInfo ci, @Local(name = "str") MutableComponent str) {
        if (lastToolHighlight.isEnchanted())
            str.withStyle(ChatFormatting.BOLD);
    }

    @ModifyArg(method = "extractCrosshair", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
            ordinal = 0), index = 1)
    private Identifier modifyCrosshairSprite(Identifier location) {
        HitResult hitResult = minecraft.hitResult;
        return hitResult != null && hitResult.getType() == HitResult.Type.BLOCK ? location : CROSSHAIR_AIR_SPRITE;
    }

    @Inject(method = "tick()V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private void decreaseRecentDamageTick(CallbackInfo ci) {
        if (recentDamageTick > 0)
            recentDamageTick--;
    }

    @Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"))
    private void renderCrosshairHitmarker(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (recentDamageTick <= 0 || hitmarkerSprite == null)
            return;

        int color = ARGB.multiplyAlpha(hitmarkerColor, (float) recentDamageTick / RECENT_DAMAGE_DURATION);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, hitmarkerSprite, (graphics.guiWidth() - CROSSHAIR_HITMARKER_SIZE) / 2,
                (graphics.guiHeight() - CROSSHAIR_HITMARKER_SIZE) / 2, CROSSHAIR_HITMARKER_SIZE, CROSSHAIR_HITMARKER_SIZE, color);
    }
}
