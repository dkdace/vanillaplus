package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.data.LevelBasedValuePreset;
import com.dace.vanillaplus.extension.world.item.component.VPTooltipProvider;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
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
    private static void applyExtraDescrption(@NonNull Consumer<Component> componentConsumer, @NonNull Holder<Enchantment> enchantmentHolder,
                                             int level) {
        Enchantment enchantment = enchantmentHolder.value();

        enchantmentHolder.unwrapKey()
                .map(LevelBasedValuePreset::fromResourceKey)
                .ifPresent(levelBasedValuePreset ->
                        VPTooltipProvider.applyComponent(componentConsumer, enchantment.description(), levelBasedValuePreset, level));

        enchantment.getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach(enchantmentAttributeEffect ->
                VPTooltipProvider.applyAttributeComponent(componentConsumer, enchantmentAttributeEffect, level));
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0,
            shift = At.Shift.AFTER))
    private void addDescriptionToolTip0(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                        DataComponentGetter dataComponentGetter, CallbackInfo ci, @Local Holder<Enchantment> enchantmentHolder,
                                        @Local int level) {
        applyExtraDescrption(componentConsumer, enchantmentHolder, level);
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1,
            shift = At.Shift.AFTER))
    private void addDescriptionToolTip1(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                        DataComponentGetter dataComponentGetter, CallbackInfo ci,
                                        @Local Object2IntMap.Entry<Holder<Enchantment>> entry) {
        applyExtraDescrption(componentConsumer, entry.getKey(), entry.getIntValue());
    }
}
