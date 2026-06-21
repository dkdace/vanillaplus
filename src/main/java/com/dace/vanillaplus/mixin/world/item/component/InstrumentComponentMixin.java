package com.dace.vanillaplus.mixin.world.item.component;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.VPInstrument;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.InstrumentComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.function.Consumer;

@Mixin(InstrumentComponent.class)
public abstract class InstrumentComponentMixin implements VPMixin<InstrumentComponent> {
    @Shadow
    @Final
    private Holder<Instrument> instrument;

    @Inject(method = "addToTooltip", at = @At("TAIL"))
    private void addEffectTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components,
                                  CallbackInfo ci) {
        VPInstrument.cast(instrument.value()).getMobEffectInstance().ifPresent(mobEffectInstance ->
                PotionContents.addPotionTooltip(Collections.singletonList(mobEffectInstance), consumer, 1, context.tickRate()));
    }
}
