package com.dace.vanillaplus.mixin.world.item.component;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.VPInstrument;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.InstrumentComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(InstrumentComponent.class)
public abstract class InstrumentComponentMixin implements VPMixin<InstrumentComponent> {
    @Shadow
    public abstract Optional<Holder<Instrument>> unwrap(HolderLookup.Provider registries);

    @Inject(method = "addToTooltip", at = @At("TAIL"))
    private void addEffectTooltip(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                  DataComponentGetter dataComponentGetter, CallbackInfo ci, @Local HolderLookup.Provider registries) {
        if (registries != null)
            unwrap(registries)
                    .flatMap(instrumentHolder -> VPInstrument.cast(instrumentHolder.value()).getDataModifier())
                    .ifPresent(instrumentEffect ->
                            PotionContents.addPotionTooltip(Collections.singletonList(instrumentEffect.getMobEffectInstance()), componentConsumer,
                                    1, tooltipContext.tickRate()));
    }
}
