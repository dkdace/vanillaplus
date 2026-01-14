package com.dace.vanillaplus.mixin.world.item.equipment.trim;

import com.dace.vanillaplus.extension.world.item.component.VPTooltipProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin implements VPTooltipProvider<ArmorTrim> {
    @Shadow
    @Final
    private Holder<TrimPattern> pattern;
    @Shadow
    @Final
    private Holder<TrimMaterial> material;

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1,
            shift = At.Shift.AFTER))
    private void addTrimPatternEffectTooltip(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                             DataComponentGetter dataComponentGetter, CallbackInfo ci) {
        VPTooltipProvider.applyTrimPatternEffectsTooltip(componentConsumer, pattern);
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 2,
            shift = At.Shift.AFTER))
    private void addTrimMaterialEffectTooltip(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                              DataComponentGetter dataComponentGetter, CallbackInfo ci) {
        VPTooltipProvider.applyTrimMaterialEffectsTooltip(componentConsumer, material);
    }
}
