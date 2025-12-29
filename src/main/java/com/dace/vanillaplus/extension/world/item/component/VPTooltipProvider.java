package com.dace.vanillaplus.extension.world.item.component;

import com.dace.vanillaplus.data.ArmorTrimEffect;
import com.dace.vanillaplus.data.LevelBasedValuePreset;
import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

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
    private static void applyLevelBasedValueTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Component descriptionComponent,
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
    private static void applyAttributeEffectTooltip(@NonNull Consumer<Component> componentConsumer,
                                                    @NonNull EnchantmentAttributeEffect enchantmentAttributeEffect, int level) {
        double amount = enchantmentAttributeEffect.amount().calculate(level);
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

    /**
     * 지정한 마법 부여의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer      {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param descriptionComponent   설명 텍스트 요소
     * @param enchantmentResourceKey 마법 부여 리소스 키
     * @param enchantment            마법 부여
     * @param level                  마법 부여 레벨
     */
    static void applyEnchantmentEffectsTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Component descriptionComponent,
                                               @NonNull ResourceKey<Enchantment> enchantmentResourceKey, @NonNull Enchantment enchantment, int level) {
        LevelBasedValuePreset levelBasedValuePreset = LevelBasedValuePreset.fromEnchantment(enchantmentResourceKey);
        if (levelBasedValuePreset != null)
            applyLevelBasedValueTooltip(componentConsumer, descriptionComponent, levelBasedValuePreset, level);

        enchantment.getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach(enchantmentAttributeEffect ->
                applyAttributeEffectTooltip(componentConsumer, enchantmentAttributeEffect, level));
    }

    /**
     * 지정한 갑옷 장식 형판의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param trimPatternHolder 갑옷 장식 형판 홀더 인스턴스
     */
    static void applyTrimPatternEffectsTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Holder<TrimPattern> trimPatternHolder) {
        trimPatternHolder.unwrapKey().ifPresent(trimPatternResourceKey -> {
            ArmorTrimEffect.TrimPatternEffect trimPatternEffect = ArmorTrimEffect.TrimPatternEffect.fromTrimPattern(trimPatternResourceKey);
            if (trimPatternEffect != null)
                applyEnchantmentEffectsTooltip(componentConsumer, trimPatternHolder.value().description(),
                        trimPatternEffect.getEnchantmentResourceKey(), trimPatternEffect.getEnchantmentHolder().value(), 1);
        });
    }

    /**
     * 지정한 갑옷 장식 재료의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer  {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param trimMaterialHolder 갑옷 장식 재료 홀더 인스턴스
     */
    static void applyTrimMaterialEffectsTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Holder<TrimMaterial> trimMaterialHolder) {
        trimMaterialHolder.unwrapKey().ifPresent(trimMaterialResourceKey -> {
            ArmorTrimEffect.TrimMaterialEffect trimMaterialEffect = ArmorTrimEffect.TrimMaterialEffect.fromTrimMaterial(trimMaterialResourceKey);
            if (trimMaterialEffect != null)
                applyEnchantmentEffectsTooltip(componentConsumer, trimMaterialHolder.value().description(),
                        trimMaterialEffect.getEnchantmentResourceKey(), trimMaterialEffect.getEnchantmentHolder().value(), 1);
        });
    }
}
