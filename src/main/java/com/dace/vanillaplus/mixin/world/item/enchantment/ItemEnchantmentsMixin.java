package com.dace.vanillaplus.mixin.world.item.enchantment;

import com.dace.vanillaplus.data.EnchantmentExtension;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemEnchantments.class)
public abstract class ItemEnchantmentsMixin implements VPMixin<ItemEnchantments> {
    @Unique
    private static final String COMPONENT_ENCHANTMENT_DESCRIPTION = ".description.";
    @Unique
    private static final String COMPONENT_ATTRIBUTE_MODIFIER_PLUS = "attribute.modifier.plus.";
    @Unique
    private static final String COMPONENT_ATTRIBUTE_MODIFIER_TAKE = "attribute.modifier.take.";

    @Unique
    private static void applyComponent(@NonNull Consumer<Component> componentConsumer, @NonNull Enchantment enchantment, int level,
                                       @NonNull EnchantmentExtension enchantmentExtension) {
        if (enchantment.description().getContents() instanceof TranslatableContents translatableContents)
            enchantmentExtension.getValues().forEach(definedValue -> {
                String key = translatableContents.getKey() + COMPONENT_ENCHANTMENT_DESCRIPTION + definedValue.getDescriptionIndex();
                String argument = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT
                        .format(definedValue.getLevelBasedValue().calculate(level) * definedValue.getDescriptionValueMultiplier());

                MutableComponent component = Component.translatable(key, argument);
                componentConsumer.accept(CommonComponents.space().append(component).withStyle(ChatFormatting.BLUE));
            });
    }

    @Unique
    private static void applyAttributeComponent(@NonNull Consumer<Component> componentConsumer,
                                                @NonNull EnchantmentAttributeEffect enchantmentAttributeEffect, int level) {
        double amount = enchantmentAttributeEffect.getModifier(level, EquipmentSlotGroup.ANY).amount();
        if (amount == 0)
            return;

        AttributeModifier.Operation operation = enchantmentAttributeEffect.operation();
        Holder<Attribute> attributeHolder = enchantmentAttributeEffect.attribute();

        if (operation == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || operation == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            amount *= 100;
        else if (attributeHolder == Attributes.KNOCKBACK_RESISTANCE)
            amount *= 10;

        String key;
        boolean style;
        if (amount > 0) {
            key = COMPONENT_ATTRIBUTE_MODIFIER_PLUS;
            style = true;
        } else {
            key = COMPONENT_ATTRIBUTE_MODIFIER_TAKE;
            style = false;
            amount = -amount;
        }

        Attribute attribute = attributeHolder.value();
        MutableComponent component = Component.translatable(key + operation.id(),
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(amount),
                Component.translatable(attribute.getDescriptionId()));

        componentConsumer.accept(CommonComponents.space().append(component).withStyle(attribute.getStyle(style)));
    }

    @Unique
    private static void applyExtraDescrption(@NonNull Consumer<Component> componentConsumer, @NonNull Holder<Enchantment> enchantmentHolder,
                                             int level) {
        Enchantment enchantment = enchantmentHolder.value();

        enchantmentHolder.unwrapKey()
                .map(EnchantmentExtension::fromEnchantment)
                .ifPresent(enchantmentExtension -> applyComponent(componentConsumer, enchantment, level, enchantmentExtension));

        enchantment.getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach(enchantmentAttributeEffect ->
                applyAttributeComponent(componentConsumer, enchantmentAttributeEffect, level));
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE",
            target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void addDescriptionToolTip0(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                        DataComponentGetter dataComponentGetter, CallbackInfo ci, @Local Holder<Enchantment> enchantmentHolder,
                                        @Local int level) {
        applyExtraDescrption(componentConsumer, enchantmentHolder, level);
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE",
            target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void addDescriptionToolTip1(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                        DataComponentGetter dataComponentGetter, CallbackInfo ci,
                                        @Local Object2IntMap.Entry<Holder<Enchantment>> entry) {
        applyExtraDescrption(componentConsumer, entry.getKey(), entry.getIntValue());
    }
}
