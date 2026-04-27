package com.dace.vanillaplus.mixin.client.gui.screens.inventory;

import com.dace.vanillaplus.util.DynamicComponent;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreenMixin<MerchantScreen, MerchantMenu> {
    @Unique
    private static final int STOCK_TEXT_OFFSET_X = 25;
    @Unique
    private static final int STOCK_TEXT_OFFSET_Y = 1;
    @Unique
    private static final int STOCK_HOVER_OFFSET_X = 18;
    @Unique
    private static final int STOCK_HOVER_WIDTH = 12;
    @Unique
    private static final int STOCK_HOVER_HEIGHT = 18;
    @Unique
    private static final int STOCK_COLOR_OUT_OF_STOCK = ARGB.color(255, 96, 96);
    @Unique
    private static final int STOCK_COLOR_FULL = ARGB.color(128, 255, 32);
    @Unique
    private static final int STOCK_COLOR_USED = ARGB.color(255, 255, 255);
    @Unique
    private static final DynamicComponent COMPONENT_MERCHANT_STOCK = args ->
            Component.translatable("merchant.stock", args);

    @Shadow
    @Final
    private static int SELL_ITEM_1_X;
    @Shadow
    @Final
    private static int SELL_ITEM_2_X;

    @Unique
    private static int getStockTextColor(@NonNull MerchantOffer offer) {
        int stockTextColor;
        if (offer.isOutOfStock())
            stockTextColor = STOCK_COLOR_OUT_OF_STOCK;
        else
            stockTextColor = offer.getUses() == 0 ? STOCK_COLOR_FULL : STOCK_COLOR_USED;

        return stockTextColor;
    }

    @ModifyArg(method = "extractContents", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;extractButtonArrows(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"),
            index = 3)
    private int modifyArrowIconY(int decorHeight) {
        return decorHeight + 6;
    }

    @Inject(method = "extractContents", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;extractButtonArrows(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"))
    private void renderStock(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci, @Local(name = "xo") int xo,
                             @Local(name = "decorHeight") int decorHeight, @Local(name = "offer") MerchantOffer offer) {
        int remainingUses = offer.getMaxUses() - offer.getUses();
        int x = xo + SELL_ITEM_1_X + SELL_ITEM_2_X;

        graphics.centeredText(font, Integer.toString(remainingUses), x + STOCK_TEXT_OFFSET_X, decorHeight + STOCK_TEXT_OFFSET_Y,
                getStockTextColor(offer));

        int hoverX = x + STOCK_HOVER_OFFSET_X - leftPos;
        int hoverY = decorHeight - topPos;

        if (isHovering(hoverX, hoverY, STOCK_HOVER_WIDTH, STOCK_HOVER_HEIGHT, mouseX, mouseY))
            graphics.setTooltipForNextFrame(font, COMPONENT_MERCHANT_STOCK.get(remainingUses, offer.getMaxUses()), mouseX, mouseY);
    }
}
