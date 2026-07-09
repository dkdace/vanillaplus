package com.dace.vanillaplus.mixin.client.gui.screens.options;

import com.dace.vanillaplus.extension.client.VPOptions;
import com.dace.vanillaplus.mixin.client.gui.screens.ScreenMixin;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin extends ScreenMixin<VideoSettingsScreen> {
    @ModifyReturnValue(method = "preferenceOptions", at = @At("RETURN"))
    private static OptionInstance<?>[] addExtraOptions(OptionInstance<?>[] optionInstances, @Local(argsOnly = true) Options options) {
        return ArrayUtils.addAll(optionInstances, VPOptions.cast(options).getAttackMarker(), VPOptions.cast(options).getMobHealthIndicator());
    }
}
