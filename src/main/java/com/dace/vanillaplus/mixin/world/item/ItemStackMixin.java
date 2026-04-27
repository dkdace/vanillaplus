package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.extension.world.item.VPItemStack;
import com.dace.vanillaplus.extension.world.item.alchemy.VPPotion;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimMaterial;
import com.dace.vanillaplus.util.DynamicComponent;
import com.dace.vanillaplus.world.block.BlockModifier;
import com.dace.vanillaplus.world.item.ItemModifier;
import com.dace.vanillaplus.world.item.PotionModifier;
import com.dace.vanillaplus.world.item.component.ExtraFood;
import com.dace.vanillaplus.world.item.component.RepairWithXP;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
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
    private static final String TAG_MINEABLE = "mineable";
    @Unique
    private static final Component COMPONENT_TOOL_WHEN_BREAKING = Component.translatable("tool.when_breaking")
            .withStyle(ChatFormatting.GRAY);
    @Unique
    private static final DynamicComponent COMPONENT_ATTRIBUTE_MODIFIER = args ->
            CommonComponents.space()
                    .append(Component.translatable("attribute.modifier.equals.0", args))
                    .withStyle(ChatFormatting.DARK_GREEN);
    @Unique
    private static final Component COMPONENT_ATTACK_RANGE_WHEN_ATTACKING = Component.translatable("attack_range.when_attacking")
            .withStyle(ChatFormatting.GRAY);
    @Unique
    private static final DynamicComponent COMPONENT_ATTACK_RANGE_MIN_REACH = args ->
            CommonComponents.space()
                    .append(Component.translatable("attack_range.min_reach", args))
                    .withStyle(ChatFormatting.DARK_GREEN);
    @Unique
    private static final DynamicComponent COMPONENT_ATTACK_RANGE_MAX_REACH = args ->
            CommonComponents.space()
                    .append(Component.translatable("attack_range.max_reach", args))
                    .withStyle(ChatFormatting.DARK_GREEN);
    @Unique
    private static final DynamicComponent COMPONENT_ATTACK_RANGE_RANGE = args ->
            CommonComponents.space()
                    .append(Component.translatable("attack_range.range", args))
                    .withStyle(ChatFormatting.DARK_GREEN);
    @Unique
    private static final Component COMPONENT_KINETIC_WEAPON_WHEN_CHARGING = Component.translatable("kinetic_weapon.when_charging")
            .withStyle(ChatFormatting.GRAY);
    @Unique
    private static final DynamicComponent COMPONENT_KINETIC_WEAPON_DAMAGE_MULTIPLIER = args ->
            CommonComponents.space()
                    .append(Component.translatable("kinetic_weapon.damage_multiplier", args))
                    .withStyle(ChatFormatting.DARK_GREEN);
    @Unique
    private static final Component COMPONENT_WEAPON_SHIELD_DISARMING = Component.translatable("weapon.shield_disarming")
            .withStyle(ChatFormatting.GRAY);
    @Unique
    private static final DynamicComponent COMPONENT_WEAPON_SHIELD_DISARMING_TIME = args ->
            CommonComponents.space()
                    .append(Component.translatable("weapon.shield_disarming_time", args))
                    .withStyle(ChatFormatting.DARK_GREEN);
    @Unique
    private static final Component COMPONENT_FOOD_WHEN_EATEN = Component.translatable("food.when_eaten")
            .withStyle(ChatFormatting.DARK_PURPLE);
    @Unique
    private static final DynamicComponent COMPONENT_FOOD_NUTRITION = args ->
            Component.translatable("food.nutrition", args).withStyle(ChatFormatting.BLUE);
    @Unique
    private static final DynamicComponent COMPONENT_FOOD_SATURATION = args ->
            Component.translatable("food.saturation", args).withStyle(ChatFormatting.BLUE);
    @Unique
    private static final DynamicComponent COMPONENT_CONSUMABLE_REMOVE_STATUS_EFFECT = args ->
            Component.translatable("consumable.remove_status_effect", args);
    @Unique
    private static final Component COMPONENT_CONSUMABLE_CLEAR_ALL_STATUS_EFFECTS = Component.translatable("consumable.clear_all_status_effects")
            .withStyle(MobEffectCategory.NEUTRAL.getTooltipFormatting());
    @Unique
    private static final DynamicComponent COMPONENT_CONSUMABLE_APPLY_STATUS_EFFECTS = args ->
            Component.translatable("consumable.apply_status_effects", args).withStyle(ChatFormatting.DARK_PURPLE);
    @Unique
    private static final Component COMPONENT_CONSUMABLE_TELEPORT_RANDOMLY = Component.translatable("consumable.teleport_randomly")
            .withStyle(MobEffectCategory.NEUTRAL.getTooltipFormatting());
    @Unique
    private static final DynamicComponent COMPONENT_EXTRA_CONSUMABLE_ADDED = args ->
            Component.translatable("extra_consumable.added", args).withStyle(ChatFormatting.GRAY);
    @Unique
    private static final Component COMPONENT_PROJECTILE_WEAPON_WHEN_SHOOT = Component.translatable("item.projectile_weapon.when_shoot")
            .withStyle(ChatFormatting.GRAY);
    @Unique
    private static final DynamicComponent COMPONENT_PROJECTILE_WEAPON_BASE_DAMAGE = args ->
            CommonComponents.space()
                    .append(Component.translatable("item.projectile_weapon.base_damage", args))
                    .withStyle(ChatFormatting.DARK_GREEN);
    @Unique
    private static final DynamicComponent COMPONENT_PROJECTILE_WEAPON_SPEED = args ->
            CommonComponents.space()
                    .append(Component.translatable("item.projectile_weapon.speed", args))
                    .withStyle(ChatFormatting.DARK_GREEN);
    @Unique
    private static final Component COMPONENT_TRIM_MATERIAL = Component.translatable("item.trim_material")
            .withStyle(ChatFormatting.GRAY);
    @Unique
    private static final DynamicComponent COMPONENT_REPAIR_LIMIT = args ->
            Component.translatable("item.repair_limit", args);

    @Unique
    private static void addToolTooltip(@NonNull Tool tool, @NonNull Consumer<Component> componentConsumer) {
        tool.rules().forEach(rule -> rule.blocks().unwrapKey().ifPresent(blockTagKey -> {
            if (!blockTagKey.location().getPath().startsWith(TAG_MINEABLE))
                return;

            componentConsumer.accept(Component.empty());
            componentConsumer.accept(COMPONENT_TOOL_WHEN_BREAKING);
            componentConsumer.accept(COMPONENT_ATTRIBUTE_MODIFIER.get(
                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(rule.speed().orElse(tool.defaultMiningSpeed())),
                    Component.translatable(Attributes.MINING_EFFICIENCY.value().getDescriptionId())));
        }));
    }

    @Unique
    private static void addAttackRangeTooltip(@NonNull AttackRange attackRange, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(Component.empty());
        componentConsumer.accept(COMPONENT_ATTACK_RANGE_WHEN_ATTACKING);

        float minRange = attackRange.minReach();
        if (minRange > 0)
            componentConsumer.accept(COMPONENT_ATTACK_RANGE_MIN_REACH.get(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(minRange)));

        componentConsumer.accept(COMPONENT_ATTACK_RANGE_MAX_REACH.get(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(attackRange.maxReach())));

        float hitboxMargin = attackRange.hitboxMargin();
        if (hitboxMargin > 0)
            componentConsumer.accept(COMPONENT_ATTACK_RANGE_RANGE.get(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(hitboxMargin)));
    }

    @Unique
    private static void addKineticWeaponTooltip(@NonNull KineticWeapon kineticWeapon, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(Component.empty());
        componentConsumer.accept(COMPONENT_KINETIC_WEAPON_WHEN_CHARGING);
        componentConsumer.accept(COMPONENT_KINETIC_WEAPON_DAMAGE_MULTIPLIER.get(
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(kineticWeapon.damageMultiplier())));
    }

    @Unique
    private static void addWeaponTooltip(@NonNull Weapon weapon, @NonNull Consumer<Component> componentConsumer) {
        float disableBlockingForSeconds = weapon.disableBlockingForSeconds();
        if (disableBlockingForSeconds <= 0)
            return;

        componentConsumer.accept(Component.empty());
        componentConsumer.accept(COMPONENT_WEAPON_SHIELD_DISARMING);
        componentConsumer.accept(COMPONENT_WEAPON_SHIELD_DISARMING_TIME.get(
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(disableBlockingForSeconds)));
    }

    @Unique
    private static void addFoodTooltip(@NonNull FoodProperties foodProperties, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(Component.empty());
        componentConsumer.accept(COMPONENT_FOOD_WHEN_EATEN);
        componentConsumer.accept(COMPONENT_FOOD_NUTRITION.get(
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(foodProperties.nutrition())));
        componentConsumer.accept(COMPONENT_FOOD_SATURATION.get(
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(foodProperties.saturation())));
    }

    @Unique
    private static void addConsumableTooltip(@NonNull Consumable consumable, @NonNull Item.TooltipContext tooltipContext,
                                             @NonNull Consumer<Component> componentConsumer) {
        consumable.onConsumeEffects().forEach(consumeEffect -> {
            switch (consumeEffect) {
                case RemoveStatusEffectsConsumeEffect removeStatusEffectsConsumeEffect ->
                        removeStatusEffectsConsumeEffect.effects().forEach(mobEffectHolder -> {
                            MobEffectCategory mobEffectCategory = switch (mobEffectHolder.value().getCategory()) {
                                case BENEFICIAL -> MobEffectCategory.HARMFUL;
                                case HARMFUL -> MobEffectCategory.BENEFICIAL;
                                default -> MobEffectCategory.NEUTRAL;
                            };

                            componentConsumer.accept(COMPONENT_CONSUMABLE_REMOVE_STATUS_EFFECT.get(
                                    mobEffectHolder.value().getDisplayName()).withStyle(mobEffectCategory.getTooltipFormatting()));
                        });
                case ClearAllStatusEffectsConsumeEffect _ -> componentConsumer.accept(COMPONENT_CONSUMABLE_CLEAR_ALL_STATUS_EFFECTS);
                case ApplyStatusEffectsConsumeEffect applyStatusEffectsConsumeEffect -> {
                    float probability = applyStatusEffectsConsumeEffect.probability();
                    if (probability < 1) {
                        componentConsumer.accept(Component.empty());
                        componentConsumer.accept(COMPONENT_CONSUMABLE_APPLY_STATUS_EFFECTS.get(
                                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(probability * 100)));
                    }

                    PotionContents.addPotionTooltip(applyStatusEffectsConsumeEffect.effects(), componentConsumer, 1,
                            tooltipContext.tickRate());
                }
                case TeleportRandomlyConsumeEffect _ -> componentConsumer.accept(COMPONENT_CONSUMABLE_TELEPORT_RANDOMLY);
                default -> {
                    // 미사용
                }
            }
        });
    }

    @Unique
    private static void addExtraFoodTooltip(@NonNull ExtraFood extraFood, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(Component.empty());
        componentConsumer.accept(COMPONENT_EXTRA_CONSUMABLE_ADDED.get(extraFood.getNameComponent()));

        FoodProperties foodProperties = extraFood.getFoodProperties();
        if (foodProperties == null)
            return;

        componentConsumer.accept(CommonComponents.space().append(COMPONENT_FOOD_NUTRITION.get(
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(foodProperties.nutrition()))));
        componentConsumer.accept(CommonComponents.space().append(COMPONENT_FOOD_SATURATION.get(
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(foodProperties.saturation()))));
    }

    @Unique
    private static void addProjectileWeaponTooltip(@NonNull ProjectileWeaponItem projectileWeaponItem, @NonNull Consumer<Component> componentConsumer) {
        VPModifiableData.getDataModifier(projectileWeaponItem, ItemModifier.ProjectileWeaponModifier.class)
                .ifPresent(projectileWeaponModifier -> {
                    componentConsumer.accept(Component.empty());
                    componentConsumer.accept(COMPONENT_PROJECTILE_WEAPON_WHEN_SHOOT);
                    componentConsumer.accept(COMPONENT_PROJECTILE_WEAPON_BASE_DAMAGE.get(
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(projectileWeaponModifier.getBaseDamage())));
                    componentConsumer.accept(COMPONENT_PROJECTILE_WEAPON_SPEED.get(
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(projectileWeaponModifier.getShootingPower())));
                });
    }

    @Unique
    private static void addTrimMaterialTooltip(@NonNull Holder<TrimMaterial> providesTrimMaterialHolder, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(COMPONENT_TRIM_MATERIAL);
        VPTrimMaterial.cast(providesTrimMaterialHolder.value()).applyTooltip(componentConsumer);
    }

    @Shadow
    @Nullable
    public abstract <T> T set(DataComponentType<T> type, @Nullable T value);

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean isEnchanted();

    @Shadow
    public abstract int getUseDuration(LivingEntity user);

    @Unique
    private <T> void addTooltip(@NonNull DataComponentType<T> dataComponentType, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<T> onAdd) {
        if (!tooltipDisplay.shows(dataComponentType))
            return;

        T component = getThis().get(dataComponentType);
        if (component != null)
            onAdd.accept(component);
    }

    @Override
    public int getRepairLimit() {
        return Math.clamp(getThis().getOrDefault(VPDataComponentTypes.REPAIR_LIMIT.get(), 0), 0, getMaxRepairLimit());
    }

    @Override
    public void setRepairLimit(int repairLimit) {
        set(VPDataComponentTypes.REPAIR_LIMIT.get(), Math.clamp(repairLimit, 0, getMaxRepairLimit()));
    }

    @Override
    public int getMaxRepairLimit() {
        RepairWithXP repairWithXP = getThis().get(VPDataComponentTypes.REPAIR_WITH_XP.get());
        Integer maxDamage = getThis().get(DataComponents.MAX_DAMAGE);

        if (repairWithXP == null || maxDamage == null)
            return 0;

        return (int) (maxDamage * repairWithXP.maxRepairLimitRatio());
    }

    @ModifyExpressionValue(method = "getRarity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchanted()Z"))
    public boolean removeRarityCondition(boolean isEnchanted) {
        return false;
    }

    @ModifyExpressionValue(method = "hasFoil", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item;isFoil(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean modifyFoilState(boolean isFoil) {
        PotionContents potionContents = getThis().get(DataComponents.POTION_CONTENTS);
        if (potionContents == null)
            return isFoil;

        return potionContents.potion().flatMap(potionHolder ->
                VPPotion.cast(potionHolder.value()).getDataModifier().map(PotionModifier::isGlistering)).orElse(isFoil);
    }

    @Inject(method = {"getStyledHoverName", "getDisplayName"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
    private void applyEnchantmentStyle(CallbackInfoReturnable<Component> cir, @Local(name = "hoverName") MutableComponent hoverName) {
        if (isEnchanted())
            hoverName.withStyle(ChatFormatting.BOLD);
    }

    @Redirect(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"))
    private void removeEnchantmentAttributeDisplay(ItemStack itemStack, EquipmentSlotGroup slot,
                                                   BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "FIELD",
            target = "Lnet/minecraft/core/component/DataComponents;POTION_CONTENTS:Lnet/minecraft/core/component/DataComponentType;",
            opcode = Opcodes.GETSTATIC))
    private void addExtraTooltips0(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag,
                                   Consumer<Component> builder, CallbackInfo ci) {
        addTooltip(DataComponents.CONSUMABLE, display, consumable -> addConsumableTooltip(consumable, context, builder));
        addTooltip(DataComponents.FOOD, display, foodProperties -> addFoodTooltip(foodProperties, builder));
        addTooltip(VPDataComponentTypes.EXTRA_FOOD.get(), display, extraFood -> addExtraFoodTooltip(extraFood, builder));

        if (getThis().is(Items.CAKE))
            VPModifiableData.getDataModifier(Blocks.CAKE, BlockModifier.CakeModifier.class).ifPresent(cakeModifier ->
                    addFoodTooltip(cakeModifier.getFoodProperties(), builder));

        addTooltip(DataComponents.PROVIDES_TRIM_MATERIAL, display, providesTrimMaterial ->
                addTrimMaterialTooltip(providesTrimMaterial, builder));
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;)V"))
    private void addExtraTooltips1(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag,
                                   Consumer<Component> builder, CallbackInfo ci) {
        addTooltip(DataComponents.TOOL, display, tool -> addToolTooltip(tool, builder));

        if (getItem() instanceof ProjectileWeaponItem projectileWeaponItem)
            addProjectileWeaponTooltip(projectileWeaponItem, builder);
    }

    @Inject(method = "addAttributeTooltips", at = @At("TAIL"))
    private void addExtraTooltipsAfterAttribute(Consumer<Component> consumer, TooltipDisplay display, @Nullable Player player, CallbackInfo ci) {
        addTooltip(DataComponents.ATTACK_RANGE, display, attackRange -> addAttackRangeTooltip(attackRange, consumer));
        addTooltip(DataComponents.KINETIC_WEAPON, display, kineticWeapon -> addKineticWeaponTooltip(kineticWeapon, consumer));
        addTooltip(DataComponents.WEAPON, display, weapon -> addWeaponTooltip(weapon, consumer));
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z"))
    private void addRepairLimitTooltip(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag,
                                       Consumer<Component> builder, CallbackInfo ci) {
        if (isRepairLimitBarVisible() && display.shows(VPDataComponentTypes.REPAIR_LIMIT.get()))
            builder.accept(COMPONENT_REPAIR_LIMIT.get(getMaxRepairLimit() - getRepairLimit(), getMaxRepairLimit()));
    }

    @Redirect(method = "onUseTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/component/Consumable;shouldEmitParticlesAndSounds(I)Z"))
    private boolean redirectEatingEffectCheck(Consumable instance, int useItemRemainingTicks, @Local(argsOnly = true) LivingEntity livingEntity) {
        int duration = getUseDuration(livingEntity);
        return duration - useItemRemainingTicks > duration * Consumable.CONSUME_EFFECTS_START_FRACTION
                && useItemRemainingTicks % Consumable.CONSUME_EFFECTS_INTERVAL == 0;
    }
}
