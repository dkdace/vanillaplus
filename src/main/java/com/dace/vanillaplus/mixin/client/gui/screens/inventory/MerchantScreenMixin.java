package com.dace.vanillaplus.mixin.client.gui.screens.inventory;

import com.llamalad7.mixinextras.sugar.Local;
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

import java.util.function.BiFunction;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreenMixin<MerchantScreen, MerchantMenu> {
    @Unique
    private static final int COLOR_OUT_OF_STOCK = ARGB.color(255, 96, 96);
    @Unique
    private static final int COLOR_FULL = ARGB.color(128, 255, 32);
    @Unique
    private static final int COLOR_USED = ARGB.color(255, 255, 255);
    @Unique
    private static final BiFunction<Object, Object, Component> COMPONENT_MERCHANT_STOCK = (arg1, arg2) ->
            Component.translatable("merchant.stock", arg1, arg2);

    @Shadow
    @Final
    private static int SELL_ITEM_1_X;
    @Shadow
    @Final
    private static int SELL_ITEM_2_X;

    @ModifyArg(method = "extractContents", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;extractButtonArrows(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"),
            index = 3)
    private int modifyArrowIconY(int y) {
        return y + 6;
    }

    @Inject(method = "extractContents", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;extractButtonArrows(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"))
    private void renderStock(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci,
                             @Local(ordinal = 2) int x, @Local(ordinal = 7) int y, @Local MerchantOffer merchantOffer) {
        int remainingUses = merchantOffer.getMaxUses() - merchantOffer.getUses();
        int stockTextX = x + SELL_ITEM_1_X + SELL_ITEM_2_X + 25;
        int stockTextY = y + 1;
        int stockTextColor;
        if (merchantOffer.isOutOfStock())
            stockTextColor = COLOR_OUT_OF_STOCK;
        else
            stockTextColor = merchantOffer.getUses() == 0 ? COLOR_FULL : COLOR_USED;

        guiGraphicsExtractor.centeredText(font, Integer.toString(remainingUses), stockTextX, stockTextY, stockTextColor);

        int hoverX = x + SELL_ITEM_1_X + SELL_ITEM_2_X + 18 - leftPos;
        int hoverY = y - topPos;
        if (!isHovering(hoverX, hoverY, 12, 18, mouseX, mouseY))
            return;

        guiGraphicsExtractor.setTooltipForNextFrame(font, COMPONENT_MERCHANT_STOCK.apply(remainingUses, merchantOffer.getMaxUses()), mouseX, mouseY);
    }
}
