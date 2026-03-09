package com.dace.vanillaplus.data.modifier;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.Optional;

/**
 * 마법 부여를 수정하는 마법 부여 수정자 클래스.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantmentModifier {
    /** JSON 코덱 */
    private static final Codec<EnchantmentModifier> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(EnchantmentDefinition.CODEC.forGetter(EnchantmentModifier::getDefinition),
                    RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("exclusive_set")
                            .forGetter(EnchantmentModifier::getExclusiveSet),
                    EnchantmentEffectComponents.CODEC.optionalFieldOf("effects").forGetter(EnchantmentModifier::getEffects))
            .apply(instance, EnchantmentModifier::new));

    /** 마법 부여 설정 */
    @NonNull
    @Getter
    private final EnchantmentDefinition definition;
    /** 호환되지 않는 마법 부여 목록 */
    @NonNull
    @Getter
    private final Optional<HolderSet<Enchantment>> exclusiveSet;
    /** 효과 */
    @NonNull
    @Getter
    private final Optional<DataComponentMap> effects;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.ENCHANTMENT_MODIFIER.getRegistryKey(), DIRECT_CODEC, DIRECT_CODEC);
    }

    /**
     * 마법 부여 설정 클래스.
     *
     * @param supportedItems 호환 가능 아이템 목록
     * @param primaryItems   마법 부여대에서 호환 가능한 아이템 목록
     * @param weight         마법 부여대에서의 등장 확률
     * @param maxLevel       최대 레벨
     * @param minCost        최소 가격
     * @param maxCost        최대 가격
     * @param anvilCost      모루에서의 가격
     * @param slots          장착 슬롯
     */
    public record EnchantmentDefinition(@NonNull Optional<HolderSet<Item>> supportedItems, @NonNull Optional<Optional<HolderSet<Item>>> primaryItems,
                                        @NonNull Optional<Integer> weight, @NonNull Optional<Integer> maxLevel,
                                        @NonNull Optional<Enchantment.Cost> minCost,
                                        @NonNull Optional<Enchantment.Cost> maxCost, @NonNull Optional<Integer> anvilCost,
                                        @NonNull Optional<List<EquipmentSlotGroup>> slots) {
        private static final MapCodec<EnchantmentDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("supported_items")
                                .forGetter(EnchantmentDefinition::supportedItems),
                        CodecUtil.optional(RegistryCodecs.homogeneousList(Registries.ITEM)).optionalFieldOf("primary_items")
                                .forGetter(EnchantmentDefinition::primaryItems),
                        ExtraCodecs.intRange(1, 1024).optionalFieldOf("weight").forGetter(EnchantmentDefinition::weight),
                        ExtraCodecs.intRange(1, 255).optionalFieldOf("max_level").forGetter(EnchantmentDefinition::maxLevel),
                        Enchantment.Cost.CODEC.optionalFieldOf("min_cost").forGetter(EnchantmentDefinition::minCost),
                        Enchantment.Cost.CODEC.optionalFieldOf("max_cost").forGetter(EnchantmentDefinition::maxCost),
                        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("anvil_cost").forGetter(EnchantmentDefinition::anvilCost),
                        EquipmentSlotGroup.CODEC.listOf().optionalFieldOf("slots").forGetter(EnchantmentDefinition::slots))
                .apply(instance, EnchantmentDefinition::new));
    }
}
