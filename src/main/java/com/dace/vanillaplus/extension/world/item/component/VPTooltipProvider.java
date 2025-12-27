package com.dace.vanillaplus.extension.world.item.component;

import com.dace.vanillaplus.data.LevelBasedValuePreset;
import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

import java.util.function.Consumer;

/**
 * {@link TooltipProvider}를 확장하는 인터페이스.
 *
 * @param <T> {@link TooltipProvider}를 상속받는 타입
 */
public interface VPTooltipProvider<T extends TooltipProvider> extends VPMixin<T> {
    String COMPONENT_EFFECT_DESCRIPTION = ".description.";
    String COMPONENT_ATTRIBUTE_MODIFIER_PLUS = "attribute.modifier.plus.";
    String COMPONENT_ATTRIBUTE_MODIFIER_TAKE = "attribute.modifier.take.";

    /**
     * 마법 부여의 레벨 기반 값에 대한 아이템 툴팁을 추가한다.
     *
     * @param componentConsumer     {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param descriptionComponent  설명 텍스트 요소
     * @param levelBasedValuePreset 레벨 기반 값 프리셋
     * @param level                 마법 부여 레벨
     */
    static void applyComponent(@NonNull Consumer<Component> componentConsumer, @NonNull Component descriptionComponent,
                               @NonNull LevelBasedValuePreset levelBasedValuePreset, int level) {
        if (descriptionComponent.getContents() instanceof TranslatableContents translatableContents)
            levelBasedValuePreset.getValues().forEach(definedValue -> {
                String key = translatableContents.getKey() + COMPONENT_EFFECT_DESCRIPTION + definedValue.getDescriptionIndex();
                String argument = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT
                        .format(definedValue.getLevelBasedValue().calculate(level) * definedValue.getDescriptionValueMultiplier());

                MutableComponent component = Component.translatable(key, argument);
                componentConsumer.accept(CommonComponents.space().append(component).withStyle(ChatFormatting.BLUE));
            });
    }

    /**
     * 엔티티 속성 마법 부여 효과에 대한 아이템 툴팁을 추가한다.
     *
     * @param componentConsumer          {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param enchantmentAttributeEffect 엔티티 속성 마법 부여 효과
     * @param level                      마법 부여 레벨
     */
    static void applyAttributeComponent(@NonNull Consumer<Component> componentConsumer, @NonNull EnchantmentAttributeEffect enchantmentAttributeEffect,
                                        int level) {
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
}
