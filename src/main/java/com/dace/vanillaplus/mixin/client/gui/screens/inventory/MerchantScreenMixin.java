package com.dace.vanillaplus.mixin.client.gui.screens.inventory;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
    private static final int COLOR_OUT_OF_STOCK = ARGB.color(255, 96, 96);
    @Unique
    private static final int COLOR_FULL = ARGB.color(128, 255, 32);
    @Unique
    private static final int COLOR_USED = ARGB.color(255, 255, 255);
    @Unique
    private static final String COMPONENT_MERCHANT_STOCK = "merchant.stock";

    @Shadow
    @Final
    private static int SELL_ITEM_1_X;
    @Shadow
    @Final
    private static int SELL_ITEM_2_X;

    @ModifyArg(method = "renderContents", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;renderButtonArrows(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"),
            index = 3)
    private int modifyArrowIconY(int y) {
        return y + 6;
    }

    @Inject(method = "renderContents", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;renderButtonArrows(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"))
    private void renderStock(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci, @Local(ordinal = 2) int x,
                             @Local(ordinal = 7) int y, @Local MerchantOffer merchantOffer) {
        int remainingUses = merchantOffer.getMaxUses() - merchantOffer.getUses();
        int stockTextX = x + SELL_ITEM_1_X + SELL_ITEM_2_X + 25;
        int stockTextY = y + 1;
        int stockTextColor;
        if (merchantOffer.isOutOfStock())
            stockTextColor = COLOR_OUT_OF_STOCK;
        else
            stockTextColor = merchantOffer.getUses() == 0 ? COLOR_FULL : COLOR_USED;

        guiGraphics.drawCenteredString(font, Integer.toString(remainingUses), stockTextX, stockTextY, stockTextColor);

        int hoverX = x + SELL_ITEM_1_X + SELL_ITEM_2_X + 18 - leftPos;
        int hoverY = y - topPos;
        if (!isHovering(hoverX, hoverY, 12, 18, mouseX, mouseY))
            return;

        MutableComponent tooltipComponent = Component.translatable(COMPONENT_MERCHANT_STOCK, remainingUses, merchantOffer.getMaxUses());
        guiGraphics.setTooltipForNextFrame(font, tooltipComponent, mouseX, mouseY);
    }
}
