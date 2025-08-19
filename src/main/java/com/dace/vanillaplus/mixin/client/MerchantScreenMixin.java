package com.dace.vanillaplus.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreenMixin {
    @Shadow
    @Final
    private static int SELL_ITEM_1_X;
    @Shadow
    @Final
    private static int SELL_ITEM_2_X;

    @ModifyArg(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;renderButtonArrows(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"),
            index = 3)
    private int modifyArrowIconY(int y) {
        return y + 6;
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;renderButtonArrows(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"))
    private void renderStock(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci, @Local(ordinal = 2) int x,
                             @Local(ordinal = 7) int y, @Local MerchantOffer merchantOffer) {
        String remainingUses = Integer.toString(merchantOffer.getMaxUses() - merchantOffer.getUses());
        String maxUses = Integer.toString(merchantOffer.getMaxUses());

        Component stockComponent = Component.literal(remainingUses);
        int stockTextX = x + SELL_ITEM_1_X + SELL_ITEM_2_X + 25;
        int stockTextY = y + 1;
        int stockTextColor;
        if (merchantOffer.isOutOfStock())
            stockTextColor = ARGB.color(0xff, 0x60, 0x60);
        else
            stockTextColor = remainingUses.equals(maxUses)
                    ? ARGB.color(0x80, 0xff, 0x20)
                    : ARGB.color(0xff, 0xff, 0xff);

        guiGraphics.drawCenteredString(font, stockComponent, stockTextX, stockTextY, stockTextColor);

        int hoverX = x + SELL_ITEM_1_X + SELL_ITEM_2_X + 18 - leftPos;
        int hoverY = y - topPos;
        if (!isHovering(hoverX, hoverY, 12, 18, mouseX, mouseY))
            return;

        MutableComponent tooltipComponent = Component.translatable("merchant.stock", remainingUses, maxUses);
        guiGraphics.setTooltipForNextFrame(font, tooltipComponent, mouseX, mouseY);
    }
}
