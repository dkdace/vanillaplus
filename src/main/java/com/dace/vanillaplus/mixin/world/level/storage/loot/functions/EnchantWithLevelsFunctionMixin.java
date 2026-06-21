package com.dace.vanillaplus.mixin.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantWithLevelsFunction.class)
public abstract class EnchantWithLevelsFunctionMixin extends LootItemConditionalFunctionMixin<EnchantWithLevelsFunction> {
    @Shadow
    @Final
    public static final MapCodec<EnchantWithLevelsFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
                    .and(instance.group(NumberProviders.CODEC.fieldOf("levels")
                                    .forGetter(enchantWithLevelsFunction ->
                                            ((EnchantWithLevelsFunctionMixin) (Object) enchantWithLevelsFunction).levels),
                            RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("options")
                                    .forGetter(enchantWithLevelsFunction ->
                                            ((EnchantWithLevelsFunctionMixin) (Object) enchantWithLevelsFunction).options),
                            Codec.BOOL.optionalFieldOf("include_additional_cost_component", false)
                                    .forGetter(enchantWithLevelsFunction ->
                                            ((EnchantWithLevelsFunctionMixin) (Object) enchantWithLevelsFunction).includeAdditionalCostComponent),
                            ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("additional_cost_multiplier", 1F)
                                    .forGetter(enchantWithLevelsFunction ->
                                            ((EnchantWithLevelsFunctionMixin) (Object) enchantWithLevelsFunction).additionalCostMultiplier)))
                    .apply(instance, EnchantWithLevelsFunctionMixin::create));

    @Shadow
    @Final
    private NumberProvider levels;
    @Shadow
    @Final
    private Optional<HolderSet<Enchantment>> options;
    @Shadow
    @Final
    private boolean includeAdditionalCostComponent;
    @Unique
    private float additionalCostMultiplier;

    @Unique
    @NonNull
    private static EnchantWithLevelsFunction create(List<LootItemCondition> predicates, NumberProvider levels,
                                                    Optional<HolderSet<Enchantment>> options, boolean includeAdditionalCostComponent,
                                                    float additionalCostMultiplier) {
        EnchantWithLevelsFunction enchantWithLevelsFunction = init(predicates, levels, options, includeAdditionalCostComponent);
        ((EnchantWithLevelsFunctionMixin) (Object) enchantWithLevelsFunction).additionalCostMultiplier = additionalCostMultiplier;

        return enchantWithLevelsFunction;
    }

    @Invoker("<init>")
    private static EnchantWithLevelsFunction init(List<LootItemCondition> predicates, NumberProvider levels, Optional<HolderSet<Enchantment>> options,
                                                  boolean includeAdditionalCostComponent) {
        throw new UnsupportedOperationException();
    }

    @ModifyArg(method = "run", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"),
            index = 1)
    private Object applyCostMultiplier(@Nullable Object value) {
        return value == null ? null : (int) ((int) value * additionalCostMultiplier);
    }
}
