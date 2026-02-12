package com.dace.vanillaplus.extension.world.item.component;

import com.dace.vanillaplus.data.modifier.LevelBasedValuePreset;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimMaterial;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimPattern;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
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
     * 지정한 마법 부여의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer    {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param descriptionComponent 설명 텍스트 요소
     * @param enchantment          마법 부여
     * @param level                마법 부여 레벨
     */
    static void applyEnchantmentEffectsTooltip(@NonNull Consumer<Component> componentConsumer,
                                               @NonNull Component descriptionComponent, @NonNull Enchantment enchantment, int level) {
        VPEnchantment.cast(enchantment).getDataModifier().ifPresent(levelBasedValuePreset ->
                applyLevelBasedValueTooltip(componentConsumer, descriptionComponent, levelBasedValuePreset, level));

        enchantment.getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach(enchantmentAttributeEffect ->
                ItemAttributeModifiers.Display.attributeModifiers().apply(component ->
                                componentConsumer.accept(CommonComponents.space().append(component)), null,
                        enchantmentAttributeEffect.attribute(), enchantmentAttributeEffect.getModifier(level, EquipmentSlotGroup.ANY)));
    }

    /**
     * 지정한 갑옷 장식 형판의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param trimPatternHolder 갑옷 장식 형판 홀더 인스턴스
     */
    static void applyTrimPatternEffectsTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Holder<TrimPattern> trimPatternHolder) {
        VPTrimPattern.cast(trimPatternHolder.value()).getDataModifier().ifPresent(trimPatternEffect ->
                applyEnchantmentEffectsTooltip(componentConsumer, trimPatternHolder.value().description(),
                        trimPatternEffect.getEnchantmentHolder().value(), 1));
    }

    /**
     * 지정한 갑옷 장식 재료의 효과에 대한 툴팁을 적용한다.
     *
     * @param componentConsumer  {@link TooltipProvider}의 텍스트 요소 Consumer
     * @param trimMaterialHolder 갑옷 장식 재료 홀더 인스턴스
     */
    static void applyTrimMaterialEffectsTooltip(@NonNull Consumer<Component> componentConsumer, @NonNull Holder<TrimMaterial> trimMaterialHolder) {
        VPTrimMaterial.cast(trimMaterialHolder.value()).getDataModifier().ifPresent(trimMaterialEffect ->
                applyEnchantmentEffectsTooltip(componentConsumer, trimMaterialHolder.value().description(),
                        trimMaterialEffect.getEnchantmentHolder().value(), 1));
    }
}
