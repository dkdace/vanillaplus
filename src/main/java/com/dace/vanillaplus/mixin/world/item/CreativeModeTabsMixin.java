package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.GeneralConfig;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreativeModeTabs.class)
public abstract class CreativeModeTabsMixin implements VPMixin<CreativeModeTabs> {
    @ModifyExpressionValue(method = "generateOminousBottles", at = @At(value = "CONSTANT", args = "intValue=4"))
    private static int modifyOminousBottleCount(int original) {
        return GeneralConfig.get().getMaxBadOmenLevel() - 1;
    }
}
