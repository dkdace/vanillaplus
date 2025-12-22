package com.dace.vanillaplus.mixin.world.item.equipment.trim;

import com.dace.vanillaplus.data.LevelBasedValuePreset;
import com.dace.vanillaplus.data.TrimMaterialEffect;
import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
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
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin implements VPMixin<ArmorTrim> {
    @Unique
    private static final String COMPONENT_EFFECT_DESCRIPTION = ".description.";
    @Unique
    private static final String COMPONENT_ATTRIBUTE_MODIFIER_PLUS = "attribute.modifier.plus.";
    @Unique
    private static final String COMPONENT_ATTRIBUTE_MODIFIER_TAKE = "attribute.modifier.take.";

    @Shadow
    @Final
    private Holder<TrimMaterial> material;

    @Unique
    private static void applyComponent(@NonNull Consumer<Component> componentConsumer, @NonNull TrimMaterial trimMaterial,
                                       @NonNull LevelBasedValuePreset levelBasedValuePreset) {
        if (trimMaterial.description().getContents() instanceof TranslatableContents translatableContents)
            levelBasedValuePreset.getValues().forEach(definedValue -> {
                String key = translatableContents.getKey() + COMPONENT_EFFECT_DESCRIPTION + definedValue.getDescriptionIndex();
                String argument = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT
                        .format(definedValue.getLevelBasedValue().calculate(1) * definedValue.getDescriptionValueMultiplier());

                MutableComponent component = Component.translatable(key, argument);
                componentConsumer.accept(Component.literal("  ").append(component).withStyle(trimMaterial.description().getStyle()));
            });
    }

    @Unique
    private static void applyAttributeComponent(@NonNull Consumer<Component> componentConsumer, @NonNull TrimMaterial trimMaterial,
                                                @NonNull EnchantmentAttributeEffect enchantmentAttributeEffect) {
        double amount = enchantmentAttributeEffect.getModifier(1, EquipmentSlotGroup.ANY).amount();
        if (amount == 0)
            return;

        AttributeModifier.Operation operation = enchantmentAttributeEffect.operation();
        Holder<Attribute> attributeHolder = enchantmentAttributeEffect.attribute();

        if (operation == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || operation == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            amount *= 100;
        else if (attributeHolder == Attributes.KNOCKBACK_RESISTANCE)
            amount *= 10;

        String key;
        if (amount > 0)
            key = COMPONENT_ATTRIBUTE_MODIFIER_PLUS;
        else {
            key = COMPONENT_ATTRIBUTE_MODIFIER_TAKE;
            amount = -amount;
        }

        Attribute attribute = attributeHolder.value();
        MutableComponent component = Component.translatable(key + operation.id(),
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(amount),
                Component.translatable(attribute.getDescriptionId()));

        componentConsumer.accept(Component.literal("  ").append(component).withStyle(trimMaterial.description().getStyle()));
    }

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 2,
            shift = At.Shift.AFTER))
    private void addDescriptionToolTip(Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag,
                                       DataComponentGetter dataComponentGetter, CallbackInfo ci) {
        TrimMaterial trimMaterial = material.value();

        material.unwrapKey().ifPresent(trimMaterialResourceKey -> {
            LevelBasedValuePreset levelBasedValuePreset = LevelBasedValuePreset.fromResourceKey(trimMaterialResourceKey);
            if (levelBasedValuePreset != null)
                applyComponent(componentConsumer, trimMaterial, levelBasedValuePreset);

            TrimMaterialEffect trimMaterialEffect = TrimMaterialEffect.fromTrimMaterial(trimMaterialResourceKey);
            if (trimMaterialEffect != null)
                trimMaterialEffect.getEnchantmentHolder().value().getEffects(EnchantmentEffectComponents.ATTRIBUTES)
                        .forEach(enchantmentAttributeEffect ->
                                applyAttributeComponent(componentConsumer, trimMaterial, enchantmentAttributeEffect));
        });
    }
}
