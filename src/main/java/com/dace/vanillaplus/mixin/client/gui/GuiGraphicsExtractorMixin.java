package com.dace.vanillaplus.mixin.client.gui;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.VPItemStack;
import com.dace.vanillaplus.item.component.RepairWithXP;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
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

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin implements VPMixin<GuiGraphicsExtractor> {
    @Unique
    private static final int ITEM_REPAIR_LIMIT_BAR_OFFSET_X = 2;
    @Unique
    private static final int ITEM_REPAIR_LIMIT_BAR_OFFSET_Y = 13;
    @Unique
    private static final int ITEM_REPAIR_LIMIT_BAR_HEIGHT = 2;

    @Shadow
    public abstract void fill(RenderPipeline pipeline, int x0, int y0, int x1, int y1, int col);

    @Inject(method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemBar(Lnet/minecraft/world/item/ItemStack;II)V"))
    private void renderItemRepairLimitBar(Font font, ItemStack itemStack, int x, int y, @Nullable String countText, CallbackInfo ci) {
        VPItemStack vpItemStack = VPItemStack.cast(itemStack);
        if (!vpItemStack.isRepairLimitBarVisible())
            return;

        RepairWithXP repairWithXP = itemStack.get(VPDataComponentTypes.REPAIR_WITH_XP.get());
        if (repairWithXP == null)
            return;

        x += ITEM_REPAIR_LIMIT_BAR_OFFSET_X;
        y += ITEM_REPAIR_LIMIT_BAR_OFFSET_Y;
        if (itemStack.isBarVisible())
            y -= ITEM_REPAIR_LIMIT_BAR_HEIGHT;

        int value = Math.round(Item.MAX_BAR_WIDTH - (float) (vpItemStack.getRepairLimit() * Item.MAX_BAR_WIDTH) / vpItemStack.getMaxRepairLimit());
        int barWidth = Math.clamp(value, 0, Item.MAX_BAR_WIDTH);

        fill(RenderPipelines.GUI, x, y, x + Item.MAX_BAR_WIDTH, y + ITEM_REPAIR_LIMIT_BAR_HEIGHT, CommonColors.BLACK);
        fill(RenderPipelines.GUI, x, y, x + barWidth, y + ITEM_REPAIR_LIMIT_BAR_HEIGHT - 1, repairWithXP.barColor());
    }
}
