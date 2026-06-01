package com.dace.vanillaplus.mixin.world.item.trading;

import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(VillagerTrade.class)
public abstract class VillagerTradeMixin implements VPMixin<VillagerTrade> {
    @Shadow
    @Final
    public static final Codec<VillagerTrade> CODEC = RecordCodecBuilder.<VillagerTrade>create(instance -> instance
            .group(TradeCost.CODEC.fieldOf("wants").forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).wants),
                    TradeCost.CODEC.optionalFieldOf("additional_wants")
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).additionalWants),
                    ItemStackTemplate.CODEC.fieldOf("gives")
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).gives),
                    NumberProviders.CODEC.lenientOptionalFieldOf("max_uses", ConstantValue.exactly(4))
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).maxUses),
                    NumberProviders.CODEC.lenientOptionalFieldOf("reputation_discount", ConstantValue.exactly(0))
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).reputationDiscount),
                    NumberProviders.CODEC.lenientOptionalFieldOf("xp", ConstantValue.exactly(1))
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).xp),
                    LootItemCondition.DIRECT_CODEC.optionalFieldOf("merchant_predicate")
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).merchantPredicate),
                    LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("given_item_modifiers", List.of())
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).givenItemModifiers),
                    RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("double_trade_price_enchantments")
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).doubleTradePriceEnchantments),
                    Codec.BOOL.optionalFieldOf("multiply_xp_by_cost", false)
                            .forGetter(villagerTrade -> ((VillagerTradeMixin) (Object) villagerTrade).multiplyXPByCost))
            .apply(instance, VillagerTradeMixin::create)).validate(Validatable.validatorForContext(LootContextParamSets.VILLAGER_TRADE));

    @Shadow
    @Final
    private TradeCost wants;
    @Shadow
    @Final
    private Optional<TradeCost> additionalWants;
    @Shadow
    @Final
    private ItemStackTemplate gives;
    @Shadow
    @Final
    private Optional<LootItemCondition> merchantPredicate;
    @Shadow
    @Final
    private List<LootItemFunction> givenItemModifiers;
    @Shadow
    @Final
    private NumberProvider maxUses;
    @Shadow
    @Final
    private NumberProvider reputationDiscount;
    @Shadow
    @Final
    private NumberProvider xp;
    @Shadow
    @Final
    private Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments;
    @Unique
    private boolean multiplyXPByCost = false;

    @Unique
    @NonNull
    private static VillagerTrade create(TradeCost wants, Optional<TradeCost> additionalWants, ItemStackTemplate gives, NumberProvider maxUses,
                                        NumberProvider reputationDiscount, NumberProvider xp, Optional<LootItemCondition> merchantPredicate,
                                        List<LootItemFunction> givenItemModifiers, Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments,
                                        boolean multiplyXPByCost) {
        VillagerTrade villagerTrade = init(wants, additionalWants, gives, maxUses, reputationDiscount, xp, merchantPredicate, givenItemModifiers,
                doubleTradePriceEnchantments);
        ((VillagerTradeMixin) (Object) villagerTrade).multiplyXPByCost = multiplyXPByCost;

        return villagerTrade;
    }

    @Invoker("<init>")
    private static VillagerTrade init(TradeCost wants, Optional<TradeCost> additionalWants, ItemStackTemplate gives, NumberProvider maxUses,
                                      NumberProvider reputationDiscount, NumberProvider xp, Optional<LootItemCondition> merchantPredicate,
                                      List<LootItemFunction> givenItemModifiers, Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments) {
        throw new UnsupportedOperationException();
    }

    @ModifyExpressionValue(method = "getOffer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/providers/number/NumberProvider;getInt(Lnet/minecraft/world/level/storage/loot/LootContext;)I",
            ordinal = 1))
    private int multiplyXPByCost(int xp, @Local(name = "itemCost") ItemCost itemCost) {
        return multiplyXPByCost ? xp * itemCost.count() : xp;
    }

    @Definition(id = "itemEnchantments", local = @Local(type = ItemEnchantments.class, name = "itemEnchantments"))
    @Expression("itemEnchantments != null")
    @ModifyExpressionValue(method = "getOffer", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean applyDoubleTradePriceForEnchantments(boolean condition, @Local(name = "result") ItemStack result,
                                                         @Local(name = "enchantments") HolderSet<Enchantment> enchantments,
                                                         @Local(name = "additionalCost") LocalIntRef additionalCost) {
        ItemEnchantments itemEnchantments = result.get(DataComponents.ENCHANTMENTS);
        if (itemEnchantments != null && itemEnchantments.keySet().stream().anyMatch(enchantments::contains)) {
            additionalCost.set(additionalCost.get() * 2);
            return false;
        }

        return condition;
    }
}
