package com.dace.vanillaplus.mixin.client.gui.screens.inventory;

import com.dace.vanillaplus.mixin.client.gui.screens.ScreenMixin;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends ScreenMixin {
    @Shadow
    protected int topPos;
    @Shadow
    protected int leftPos;

    @Shadow
    protected abstract boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY);
}
