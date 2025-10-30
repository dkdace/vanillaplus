package com.dace.vanillaplus.mixin.client.gui.screens;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public abstract class ScreenMixin<T extends Screen> implements VPMixin<T> {
    @Shadow
    protected Font font;
}
