package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.VPItemStack;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements VPItemStack {
    @Unique
    private void addToolTooltip(@NonNull Tool tool, @NonNull Consumer<Component> componentConsumer) {
        tool.rules().forEach(rule -> rule.blocks().unwrapKey().ifPresent(blockTagKey -> {
            if (!blockTagKey.location().getPath().startsWith("mineable"))
                return;

            componentConsumer.accept(Component.empty());
            componentConsumer.accept(Component.translatable("tool.when_breaking").withStyle(ChatFormatting.GRAY));

            MutableComponent component = Component.translatable("attribute.modifier.equals.0",
                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(rule.speed().orElse(tool.defaultMiningSpeed())),
                    Component.translatable("attribute.name.mining_efficiency"));

            componentConsumer.accept(CommonComponents.space().append(component).withStyle(ChatFormatting.DARK_GREEN));
        }));
    }

    @Unique
    private void addFoodTooltip(@NonNull FoodProperties foodProperties, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(Component.empty());

        componentConsumer.accept(Component.translatable("food.whenEat").withStyle(ChatFormatting.DARK_PURPLE));
        componentConsumer.accept(Component.translatable("food.nutrition",
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(foodProperties.nutrition())).withStyle(ChatFormatting.BLUE));
        componentConsumer.accept(Component.translatable("food.saturation",
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(foodProperties.saturation())).withStyle(ChatFormatting.BLUE));
    }

    @Unique
    private void addConsumableTooltip(@NonNull Consumable consumable, @NonNull Item.TooltipContext tooltipContext,
                                      @NonNull Consumer<Component> componentConsumer) {
        consumable.onConsumeEffects().forEach(consumeEffect -> {
            switch (consumeEffect) {
                case RemoveStatusEffectsConsumeEffect removeStatusEffectsConsumeEffect ->
                        removeStatusEffectsConsumeEffect.effects().forEach(mobEffectHolder ->
                                componentConsumer.accept(Component.translatable("consumable.removeStatusEffect",
                                        mobEffectHolder.value().getDisplayName()).withStyle(ChatFormatting.BLUE)));
                case ClearAllStatusEffectsConsumeEffect clearAllStatusEffectsConsumeEffect ->
                        componentConsumer.accept(Component.translatable("consumable.clearAllStatusEffects").withStyle(ChatFormatting.BLUE));
                case ApplyStatusEffectsConsumeEffect applyStatusEffectsConsumeEffect when applyStatusEffectsConsumeEffect.probability() == 1 ->
                        PotionContents.addPotionTooltip(applyStatusEffectsConsumeEffect.effects(), componentConsumer, 1,
                                tooltipContext.tickRate());
                default -> {
                    // 미사용
                }
            }
        });
    }

    @Unique
    private void addProjectileWeaponTooltip(@NonNull Consumer<Component> componentConsumer) {
        if (!(getItem() instanceof ProjectileWeaponItem projectileWeaponItem))
            return;

        ItemModifier.ProjectileWeaponModifier projectileWeaponModifier = VPRegistries.getValue(ItemModifier.fromItem(projectileWeaponItem));
        if (projectileWeaponModifier == null)
            return;

        componentConsumer.accept(Component.empty());
        componentConsumer.accept(Component.translatable("item.projectileWeapon.when_shoot").withStyle(ChatFormatting.GRAY));

        MutableComponent component = Component.translatable("item.projectileWeapon.damage",
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(projectileWeaponModifier.getShootingPower() * 2));

        componentConsumer.accept(CommonComponents.space().append(component).withStyle(ChatFormatting.DARK_GREEN));
    }

    @Unique
    private <T> void addTooltip(@NonNull DataComponentType<T> dataComponentType, @NonNull TooltipDisplay tooltipDisplay,
                                @NonNull Consumer<T> onAdd) {
        if (!tooltipDisplay.shows(dataComponentType))
            return;

        T component = self().get(dataComponentType);
        if (component != null)
            onAdd.accept(component);
    }

    @Shadow
    @Nullable
    public abstract <T> T set(DataComponentType<T> dataComponentType, @Nullable T value);

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean isEnchanted();

    @Overwrite
    public Rarity getRarity() {
        return self().getOrDefault(DataComponents.RARITY, Rarity.COMMON);
    }

    @Inject(method = {"getStyledHoverName", "getDisplayName"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
    private void applyEnchantmentStyle(CallbackInfoReturnable<Component> cir, @Local MutableComponent mutableComponent) {
        if (isEnchanted())
            mutableComponent.withStyle(ChatFormatting.BOLD);
    }

    @Redirect(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"))
    private void removeEnchantmentAttributeDisplay(ItemStack itemStack, EquipmentSlotGroup equipmentSlotGroup,
                                                   BiConsumer<Holder<Attribute>, AttributeModifier> onApply) {
        // 미사용
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "FIELD",
            target = "Lnet/minecraft/core/component/DataComponents;POTION_CONTENTS:Lnet/minecraft/core/component/DataComponentType;"))
    private void addExtraTooltips0(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player, TooltipFlag tooltipFlag,
                                   Consumer<Component> componentConsumer, CallbackInfo ci) {
        addTooltip(DataComponents.CONSUMABLE, tooltipDisplay, consumable ->
                addConsumableTooltip(consumable, tooltipContext, componentConsumer));
        addTooltip(DataComponents.FOOD, tooltipDisplay, foodProperties -> addFoodTooltip(foodProperties, componentConsumer));
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;)V"))
    private void addExtraTooltips1(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player, TooltipFlag tooltipFlag,
                                   Consumer<Component> componentConsumer, CallbackInfo ci) {
        addTooltip(DataComponents.TOOL, tooltipDisplay, tool -> addToolTooltip(tool, componentConsumer));
        addTooltip(DataComponents.ATTRIBUTE_MODIFIERS, tooltipDisplay, itemAttributeModifiers ->
                addProjectileWeaponTooltip(componentConsumer));
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z"))
    private void addRepairLimitTooltip(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player,
                                       TooltipFlag tooltipFlag, Consumer<Component> componentConsumer, CallbackInfo ci) {
        if (EnchantmentHelper.has(self(), EnchantmentEffectComponents.REPAIR_WITH_XP)
                && tooltipDisplay.shows(VPDataComponentTypes.REPAIR_LIMIT.get()))
            componentConsumer.accept(Component.translatable("item.repairLimit",
                    getMaxRepairLimit() - getRepairLimit(),
                    getMaxRepairLimit()));
    }

    @Override
    public int getRepairLimit() {
        return Math.clamp(self().getOrDefault(VPDataComponentTypes.REPAIR_LIMIT.get(), 0), 0, getMaxRepairLimit());
    }

    @Override
    public void setRepairLimit(int repairLimit) {
        set(VPDataComponentTypes.REPAIR_LIMIT.get(), Math.clamp(repairLimit, 0, getMaxRepairLimit()));
    }

    @Override
    public int getMaxRepairLimit() {
        return self().getOrDefault(VPDataComponentTypes.MAX_REPAIR_LIMIT.get(), 0);
    }
}
