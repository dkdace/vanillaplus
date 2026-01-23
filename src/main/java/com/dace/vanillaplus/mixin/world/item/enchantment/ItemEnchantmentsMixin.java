package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.extension.world.item.component.VPTooltipProvider;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemEnchantments.class)
public abstract class ItemEnchantmentsMixin implements VPTooltipProvider<ItemEnchantments> {
    @Unique
    private int getFinalEnchantmentLevel(@NonNull DataComponentGetter dataComponentGetter, int level) {
        return level * dataComponentGetter.getOrDefault(VPDataComponentTypes.ENCHANTMENT_LEVEL_MULTIPLIER.get(), 1);
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0,
            shift = At.Shift.AFTER))
    private void addEffectsTooltip0(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                    DataComponentGetter dataComponentGetter, CallbackInfo ci, @Local Holder<Enchantment> enchantmentHolder,
                                    @Local int level) {
        VPTooltipProvider.applyEnchantmentEffectsTooltip(componentConsumer, enchantmentHolder.value().description(), enchantmentHolder.value(),
                getFinalEnchantmentLevel(dataComponentGetter, level));
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1,
            shift = At.Shift.AFTER))
    private void addEffectsTooltip1(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                    DataComponentGetter dataComponentGetter, CallbackInfo ci, @Local Object2IntMap.Entry<Holder<Enchantment>> entry) {
        Enchantment enchantment = entry.getKey().value();

        VPTooltipProvider.applyEnchantmentEffectsTooltip(componentConsumer, enchantment.description(), enchantment,
                getFinalEnchantmentLevel(dataComponentGetter, entry.getIntValue()));
    }
}
