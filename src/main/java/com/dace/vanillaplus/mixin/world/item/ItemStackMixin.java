package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.data.modifier.PotionModifier;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.extension.world.item.VPItemStack;
import com.dace.vanillaplus.extension.world.item.alchemy.VPPotion;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimMaterial;
import com.dace.vanillaplus.registryobject.VPDataComponentTypes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
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
    private static final String TAG_MINING = "mineable";
    @Unique
    private static final String COMPONENT_TOOL_WHEN_BREAKING = "tool.when_breaking";
    @Unique
    private static final String COMPONENT_ATTACK_RANGE_WHEN_ATTACKING = "attack_range.when_attacking";
    @Unique
    private static final String COMPONENT_ATTACK_RANGE_MIN_REACH = "attack_range.min_reach";
    @Unique
    private static final String COMPONENT_ATTACK_RANGE_MAX_REACH = "attack_range.max_reach";
    @Unique
    private static final String COMPONENT_ATTACK_RANGE_RANGE = "attack_range.range";
    @Unique
    private static final String COMPONENT_KINETIC_WEAPON_WHEN_CHARGING = "kinetic_weapon.when_charging";
    @Unique
    private static final String COMPONENT_KINETIC_WEAPON_DAMAGE_MULTIPLIER = "kinetic_weapon.damage_multiplier";
    @Unique
    private static final String COMPONENT_WEAPON_SHIELD_DISARMING = "weapon.shield_disarming";
    @Unique
    private static final String COMPONENT_WEAPON_SHIELD_DISARMING_TIME = "weapon.shield_disarming_time";
    @Unique
    private static final String COMPONENT_ATTRIBUTE_MODIFIER = "attribute.modifier.equals.0";
    @Unique
    private static final String COMPONENT_FOOD_WHEN_EATEN = "food.whenEaten";
    @Unique
    private static final String COMPONENT_FOOD_NUTRITION = "food.nutrition";
    @Unique
    private static final String COMPONENT_FOOD_SATURATION = "food.saturation";
    @Unique
    private static final String COMPONENT_CONSUMABLE_REMOVE_STATUS_EFFECT = "consumable.removeStatusEffect";
    @Unique
    private static final String COMPONENT_CONSUMABLE_CLEAR_ALL_STATUS_EFFECTS = "consumable.clearAllStatusEffects";
    @Unique
    private static final String COMPONENT_CONSUMABLE_APPLY_STATUS_EFFECTS = "consumable.applyStatusEffects";
    @Unique
    private static final String COMPONENT_CONSUMABLE_TELEPORT_RANDOMLY = "consumable.teleportRandomly";
    @Unique
    private static final String COMPONENT_PROJECTILE_WEAPON_WHEN_SHOOT = "item.projectileWeapon.when_shoot";
    @Unique
    private static final String COMPONENT_PROJECTILE_WEAPON_BASE_DAMAGE = "item.projectileWeapon.baseDamage";
    @Unique
    private static final String COMPONENT_PROJECTILE_WEAPON_SPEED = "item.projectileWeapon.speed";
    @Unique
    private static final String COMPONENT_TRIM_MATERIAL = "item.trim_material";
    @Unique
    private static final String COMPONENT_REPAIR_LIMIT = "item.repairLimit";

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
        VPDataComponentTypes.RepairWithXP repairWithXP = getThis().get(VPDataComponentTypes.REPAIR_WITH_XP.get());
        Integer maxDamage = getThis().get(DataComponents.MAX_DAMAGE);

        if (repairWithXP == null || maxDamage == null)
            return 0;

        return (int) (maxDamage * repairWithXP.getMaxRepairLimitRatio());
    }

    @Unique
    private void addToolTooltip(@NonNull Tool tool, @NonNull Consumer<Component> componentConsumer) {
        tool.rules().forEach(rule -> rule.blocks().unwrapKey().ifPresent(blockTagKey -> {
            if (!blockTagKey.location().getPath().startsWith(TAG_MINING))
                return;

            componentConsumer.accept(Component.empty());
            componentConsumer.accept(Component.translatable(COMPONENT_TOOL_WHEN_BREAKING).withStyle(ChatFormatting.GRAY));

            MutableComponent component = Component.translatable(COMPONENT_ATTRIBUTE_MODIFIER,
                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(rule.speed().orElse(tool.defaultMiningSpeed())),
                    Component.translatable(Attributes.MINING_EFFICIENCY.value().getDescriptionId()));

            componentConsumer.accept(CommonComponents.space().append(component).withStyle(ChatFormatting.DARK_GREEN));
        }));
    }

    @Unique
    private void addAttackRangeTooltip(@NonNull AttackRange attackRange, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(Component.empty());
        componentConsumer.accept(Component.translatable(COMPONENT_ATTACK_RANGE_WHEN_ATTACKING).withStyle(ChatFormatting.GRAY));

        float minRange = attackRange.minRange();
        if (minRange > 0) {
            MutableComponent minReachComponent = Component.translatable(COMPONENT_ATTACK_RANGE_MIN_REACH,
                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(minRange));

            componentConsumer.accept(CommonComponents.space().append(minReachComponent).withStyle(ChatFormatting.DARK_GREEN));
        }
        MutableComponent maxReachComponent = Component.translatable(COMPONENT_ATTACK_RANGE_MAX_REACH,
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(attackRange.maxRange()));

        componentConsumer.accept(CommonComponents.space().append(maxReachComponent).withStyle(ChatFormatting.DARK_GREEN));

        float hitboxMargin = attackRange.hitboxMargin();
        if (hitboxMargin > 0) {
            MutableComponent rangeComponent = Component.translatable(COMPONENT_ATTACK_RANGE_RANGE,
                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(hitboxMargin));

            componentConsumer.accept(CommonComponents.space().append(rangeComponent).withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Unique
    private void addKineticWeaponTooltip(@NonNull KineticWeapon kineticWeapon, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(Component.empty());
        componentConsumer.accept(Component.translatable(COMPONENT_KINETIC_WEAPON_WHEN_CHARGING).withStyle(ChatFormatting.GRAY));

        MutableComponent component = Component.translatable(COMPONENT_KINETIC_WEAPON_DAMAGE_MULTIPLIER,
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(kineticWeapon.damageMultiplier()));

        componentConsumer.accept(CommonComponents.space().append(component).withStyle(ChatFormatting.DARK_GREEN));
    }

    @Unique
    private void addWeaponTooltip(@NonNull Weapon weapon, @NonNull Consumer<Component> componentConsumer) {
        float disableBlockingForSeconds = weapon.disableBlockingForSeconds();
        if (disableBlockingForSeconds <= 0)
            return;

        componentConsumer.accept(Component.empty());
        componentConsumer.accept(Component.translatable(COMPONENT_WEAPON_SHIELD_DISARMING).withStyle(ChatFormatting.GRAY));

        MutableComponent component = Component.translatable(COMPONENT_WEAPON_SHIELD_DISARMING_TIME,
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(disableBlockingForSeconds));

        componentConsumer.accept(CommonComponents.space().append(component).withStyle(ChatFormatting.DARK_GREEN));
    }

    @Unique
    private void addFoodTooltip(@NonNull FoodProperties foodProperties, @NonNull Consumer<Component> componentConsumer) {
        componentConsumer.accept(Component.empty());

        componentConsumer.accept(Component.translatable(COMPONENT_FOOD_WHEN_EATEN).withStyle(ChatFormatting.DARK_PURPLE));
        componentConsumer.accept(Component.translatable(COMPONENT_FOOD_NUTRITION,
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(foodProperties.nutrition())).withStyle(ChatFormatting.BLUE));
        componentConsumer.accept(Component.translatable(COMPONENT_FOOD_SATURATION,
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(foodProperties.saturation())).withStyle(ChatFormatting.BLUE));
    }

    @Unique
    private void addConsumableTooltip(@NonNull Consumable consumable, @NonNull Item.TooltipContext tooltipContext,
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

                            componentConsumer.accept(Component.translatable(COMPONENT_CONSUMABLE_REMOVE_STATUS_EFFECT,
                                    mobEffectHolder.value().getDisplayName()).withStyle(mobEffectCategory.getTooltipFormatting()));
                        });
                case ClearAllStatusEffectsConsumeEffect ignored ->
                        componentConsumer.accept(Component.translatable(COMPONENT_CONSUMABLE_CLEAR_ALL_STATUS_EFFECTS)
                                .withStyle(MobEffectCategory.NEUTRAL.getTooltipFormatting()));
                case ApplyStatusEffectsConsumeEffect applyStatusEffectsConsumeEffect -> {
                    float probability = applyStatusEffectsConsumeEffect.probability();
                    if (probability < 1) {
                        componentConsumer.accept(Component.empty());
                        componentConsumer.accept(Component.translatable(COMPONENT_CONSUMABLE_APPLY_STATUS_EFFECTS,
                                        ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(probability * 100))
                                .withStyle(ChatFormatting.DARK_PURPLE));
                    }

                    PotionContents.addPotionTooltip(applyStatusEffectsConsumeEffect.effects(), componentConsumer, 1,
                            tooltipContext.tickRate());
                }
                case TeleportRandomlyConsumeEffect ignored -> componentConsumer.accept(Component.translatable(COMPONENT_CONSUMABLE_TELEPORT_RANDOMLY)
                        .withStyle(MobEffectCategory.NEUTRAL.getTooltipFormatting()));
                default -> {
                    // 미사용
                }
            }
        });
    }

    @Unique
    private void addProjectileWeaponTooltip(@NonNull ProjectileWeaponItem projectileWeaponItem, @NonNull Consumer<Component> componentConsumer) {
        VPModifiableData.getDataModifier(projectileWeaponItem, ItemModifier.ProjectileWeaponModifier.class)
                .ifPresent(projectileWeaponModifier -> {
                    componentConsumer.accept(Component.empty());
                    componentConsumer.accept(Component.translatable(COMPONENT_PROJECTILE_WEAPON_WHEN_SHOOT).withStyle(ChatFormatting.GRAY));

                    MutableComponent baseDamageComponent = Component.translatable(COMPONENT_PROJECTILE_WEAPON_BASE_DAMAGE,
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(projectileWeaponModifier.getBaseDamage()));
                    MutableComponent shootingPowerComponent = Component.translatable(COMPONENT_PROJECTILE_WEAPON_SPEED,
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(projectileWeaponModifier.getShootingPower()));

                    componentConsumer.accept(CommonComponents.space().append(baseDamageComponent).withStyle(ChatFormatting.DARK_GREEN));
                    componentConsumer.accept(CommonComponents.space().append(shootingPowerComponent).withStyle(ChatFormatting.DARK_GREEN));
                });
    }

    @Unique
    private void addTrimMaterialTooltip(@NonNull ProvidesTrimMaterial providesTrimMaterial, @NonNull Item.TooltipContext tooltipContext,
                                        @NonNull Consumer<Component> componentConsumer) {
        HolderLookup.Provider registries = tooltipContext.registries();

        if (registries != null)
            providesTrimMaterial.unwrap(registries).ifPresent(trimMaterialHolder -> {
                componentConsumer.accept(Component.translatable(COMPONENT_TRIM_MATERIAL).withStyle(ChatFormatting.GRAY));
                VPTrimMaterial.cast(trimMaterialHolder.value()).applyTooltip(componentConsumer);
            });
    }

    @Unique
    private <T> void addTooltip(@NonNull DataComponentType<T> dataComponentType, @NonNull TooltipDisplay tooltipDisplay,
                                @NonNull Consumer<T> onAdd) {
        if (!tooltipDisplay.shows(dataComponentType))
            return;

        T component = getThis().get(dataComponentType);
        if (component != null)
            onAdd.accept(component);
    }

    @Shadow
    @Nullable
    public abstract <T> T set(DataComponentType<T> dataComponentType, @Nullable T value);

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean is(Item item);

    @Shadow
    public abstract boolean isEnchanted();

    @Shadow
    public abstract int getUseDuration(LivingEntity livingEntity);

    @Overwrite
    public Rarity getRarity() {
        return getThis().getOrDefault(DataComponents.RARITY, Rarity.COMMON);
    }

    @ModifyExpressionValue(method = "hasFoil", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item;isFoil(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean modifyFoilState(boolean hasFoil) {
        PotionContents potionContents = getThis().get(DataComponents.POTION_CONTENTS);
        if (potionContents == null)
            return hasFoil;

        return potionContents.potion().flatMap(potionHolder ->
                VPPotion.cast(potionHolder.value()).getDataModifier().map(PotionModifier::isGlistering)).orElse(hasFoil);
    }

    @Inject(method = {"getStyledHoverName", "getDisplayName"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
    private void applyEnchantmentStyle(CallbackInfoReturnable<Component> cir, @Local MutableComponent mutableComponent) {
        if (isEnchanted())
            mutableComponent.withStyle(ChatFormatting.BOLD);
    }

    @Redirect(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"))
    private void removeEnchantmentAttributeDisplay(ItemStack itemStack, EquipmentSlotGroup equipmentSlotGroup,
                                                   BiConsumer<Holder<Attribute>, AttributeModifier> onApply) {
        // 미사용
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "FIELD",
            target = "Lnet/minecraft/core/component/DataComponents;POTION_CONTENTS:Lnet/minecraft/core/component/DataComponentType;",
            opcode = Opcodes.GETSTATIC))
    private void addExtraTooltips0(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player, TooltipFlag tooltipFlag,
                                   Consumer<Component> componentConsumer, CallbackInfo ci) {
        addTooltip(DataComponents.CONSUMABLE, tooltipDisplay, consumable ->
                addConsumableTooltip(consumable, tooltipContext, componentConsumer));
        addTooltip(DataComponents.FOOD, tooltipDisplay, foodProperties -> addFoodTooltip(foodProperties, componentConsumer));

        if (is(Items.CAKE))
            VPModifiableData.getDataModifier(Blocks.CAKE, BlockModifier.CakeModifier.class).ifPresent(cakeModifier ->
                    addFoodTooltip(cakeModifier.getFoodProperties(), componentConsumer));

        addTooltip(DataComponents.PROVIDES_TRIM_MATERIAL, tooltipDisplay, providesTrimMaterial ->
                addTrimMaterialTooltip(providesTrimMaterial, tooltipContext, componentConsumer));
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;)V"))
    private void addExtraTooltips1(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player, TooltipFlag tooltipFlag,
                                   Consumer<Component> componentConsumer, CallbackInfo ci) {
        addTooltip(DataComponents.TOOL, tooltipDisplay, tool -> addToolTooltip(tool, componentConsumer));

        if (getItem() instanceof ProjectileWeaponItem projectileWeaponItem)
            addProjectileWeaponTooltip(projectileWeaponItem, componentConsumer);
    }

    @Inject(method = "addAttributeTooltips", at = @At("TAIL"))
    private void addExtraTooltipsAfterAttribute(Consumer<Component> componentConsumer, TooltipDisplay tooltipDisplay, @Nullable Player player,
                                                CallbackInfo ci) {
        addTooltip(DataComponents.ATTACK_RANGE, tooltipDisplay, attackRange -> addAttackRangeTooltip(attackRange, componentConsumer));
        addTooltip(DataComponents.KINETIC_WEAPON, tooltipDisplay, kineticWeapon ->
                addKineticWeaponTooltip(kineticWeapon, componentConsumer));
        addTooltip(DataComponents.WEAPON, tooltipDisplay, weapon -> addWeaponTooltip(weapon, componentConsumer));
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z"))
    private void addRepairLimitTooltip(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player,
                                       TooltipFlag tooltipFlag, Consumer<Component> componentConsumer, CallbackInfo ci) {
        if (isRepairLimitBarVisible() && tooltipDisplay.shows(VPDataComponentTypes.REPAIR_LIMIT.get()))
            componentConsumer.accept(Component.translatable(COMPONENT_REPAIR_LIMIT,
                    getMaxRepairLimit() - getRepairLimit(),
                    getMaxRepairLimit()));
    }

    @Redirect(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/Consumable;shouldEmitParticlesAndSounds(I)Z"))
    private boolean redirectEatingEffectCheck(Consumable instance, int remainingDuration, @Local(argsOnly = true) LivingEntity livingEntity) {
        int duration = getUseDuration(livingEntity);
        return duration - remainingDuration > duration * 0.21875 && remainingDuration % 4 == 0;
    }
}
