package com.dace.vanillaplus.mixin.client.gui.screens.options.controls;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPOptions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ControlsScreen.class)
public abstract class ControlsScreenMixin implements VPMixin<ControlsScreen> {
    @ModifyReturnValue(method = "options", at = @At("RETURN"))
    private static OptionInstance<?>[] addExtraOptions(OptionInstance<?>[] optionInstances, @Local(argsOnly = true) Options options) {
        return ArrayUtils.add(optionInstances, VPOptions.cast(options).getToggleProne());
    }
}
