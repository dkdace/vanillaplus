package com.dace.vanillaplus.mixin.client.gui;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.VPItemStack;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements VPMixin<GuiGraphics> {
    @Unique
    private static final int COLOR_REPAIR_LIMIT_BAR = ARGB.color(50, 50, 255);

    @Shadow
    public abstract void fill(RenderPipeline renderPipeline, int minX, int minY, int maxX, int maxY, int color);

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemBar(Lnet/minecraft/world/item/ItemStack;II)V"))
    private void renderItemRepairLimitBar(Font font, ItemStack itemStack, int x, int y, @Nullable String text, CallbackInfo ci) {
        VPItemStack vpItemStack = VPItemStack.cast(itemStack);
        if (!vpItemStack.isRepairLimitBarVisible())
            return;

        x += 2;
        y += itemStack.isBarVisible() ? 11 : 13;

        int value = Math.round(Item.MAX_BAR_WIDTH - (float) (vpItemStack.getRepairLimit() * Item.MAX_BAR_WIDTH) / vpItemStack.getMaxRepairLimit());
        int barWidth = Math.clamp(value, 0, Item.MAX_BAR_WIDTH);

        fill(RenderPipelines.GUI, x, y, x + Item.MAX_BAR_WIDTH, y + 2, CommonColors.BLACK);
        fill(RenderPipelines.GUI, x, y, x + barWidth, y + 1, COLOR_REPAIR_LIMIT_BAR);
    }
}
