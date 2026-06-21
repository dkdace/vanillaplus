package com.dace.vanillaplus.mixin.core.component;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.VPItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataComponentInitializers.class)
public abstract class DataComponentInitializersMixin implements VPMixin<DataComponentInitializers> {
    @Mixin(targets = "net.minecraft.core.component.DataComponentInitializers$BakedEntry")
    private abstract static class BakedEntryMixin<T> {
        @Shadow
        @Final
        private Holder.Reference<T> element;

        @Inject(method = "apply", at = @At("TAIL"))
        private void onApply(CallbackInfo ci) {
            if (element.value() instanceof VPItem<?> vpItem)
                vpItem.applyConfigItemComponents();
        }
    }
}
