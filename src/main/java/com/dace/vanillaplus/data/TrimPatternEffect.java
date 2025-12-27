package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 갑옷 장식 형판의 효과를 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TrimPatternEffect {
    /** 레지스트리 코덱 */
    public static final Codec<Holder<TrimPatternEffect>> CODEC = VPRegistry.TRIM_PATTERN_EFFECT.createRegistryCodec();
    /** JSON 코덱 */
    private static final Codec<TrimPatternEffect> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(TrimPattern.CODEC.fieldOf("pattern").forGetter(trimPatternEffect -> trimPatternEffect.trimPatternHolder),
                    EnchantmentEffectComponents.CODEC.optionalFieldOf("effects", DataComponentMap.EMPTY)
                            .forGetter(trimPatternEffect -> trimPatternEffect.enchantmentHolder.value().effects()))
            .apply(instance, TrimPatternEffect::new));

    /** 갑옷 장식 형판 홀더 인스턴스 */
    private final Holder<TrimPattern> trimPatternHolder;
    /** 마법 부여 홀더 인스턴스 */
    @Getter
    private final Holder<Enchantment> enchantmentHolder;

    @SuppressWarnings({"unchecked", "rawtypes"})
    private TrimPatternEffect(@NonNull Holder<TrimPattern> trimPatternHolder, @NonNull DataComponentMap effectMap) {
        this.trimPatternHolder = trimPatternHolder;

        Enchantment.Builder builder = Enchantment.enchantment(Enchantment.definition(HolderSet.empty(), 0, 0,
                Enchantment.constantCost(0), Enchantment.constantCost(0), 0, EquipmentSlotGroup.ANY));

        effectMap.forEach(typedDataComponent ->
                builder.withSpecialEffect((DataComponentType) typedDataComponent.type(), typedDataComponent.value()));

        this.enchantmentHolder = Holder.direct(builder.build(trimPatternHolder.unwrapKey().map(ResourceKey::location).orElseThrow()));
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.TRIM_PATTERN_EFFECT.getRegistryKey(), DIRECT_CODEC, DIRECT_CODEC);
    }

    /**
     * 지정한 갑옷 장식 형판에 해당하는 갑옷 장식 형판 효과를 반환한다.
     *
     * @param trimPatternResourceKey 갑옷 장식 형판 리소스 키
     * @return 갑옷 장식 형판 효과. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    public static TrimPatternEffect fromTrimPattern(@NonNull ResourceKey<TrimPattern> trimPatternResourceKey) {
        return VPRegistry.TRIM_PATTERN_EFFECT.getValue(trimPatternResourceKey.location().getPath());
    }
}
