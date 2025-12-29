package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 갑옷 장식의 효과를 관리하는 클래스.
 *
 * @param <T> 갑옷 장식 재료 ({@link TrimMaterial}) 또는 형판 ({@link TrimPattern})
 */
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class ArmorTrimEffect<T> {
    /** 재료/형판 홀더 인스턴스 */
    final Holder<T> holder;
    /** 마법 부여 홀더 인스턴스 */
    @Getter
    final Holder<Enchantment> enchantmentHolder;
    /** 마법 부여 리소스 키 */
    @Getter
    private final ResourceKey<Enchantment> enchantmentResourceKey;

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ArmorTrimEffect(@NonNull Holder<T> holder, @NonNull DataComponentMap effectMap) {
        this.holder = holder;

        Enchantment.Builder builder = Enchantment.enchantment(Enchantment.definition(HolderSet.empty(), 0, 0,
                Enchantment.constantCost(0), Enchantment.constantCost(0), 0, EquipmentSlotGroup.ARMOR));

        effectMap.forEach(typedDataComponent ->
                builder.withSpecialEffect((DataComponentType) typedDataComponent.type(), typedDataComponent.value()));

        ResourceKey<T> resourceKey = holder.unwrapKey().orElseThrow();
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID,
                resourceKey.registry().getPath() + "/" + resourceKey.location().getPath());

        this.enchantmentHolder = Holder.direct(builder.build(resourceLocation));
        this.enchantmentResourceKey = ResourceKey.create(Registries.ENCHANTMENT, resourceLocation);
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.TRIM_MATERIAL_EFFECT.getRegistryKey(), TrimMaterialEffect.DIRECT_CODEC, TrimMaterialEffect.DIRECT_CODEC);
        event.dataPackRegistry(VPRegistry.TRIM_PATTERN_EFFECT.getRegistryKey(), TrimPatternEffect.DIRECT_CODEC, TrimPatternEffect.DIRECT_CODEC);
    }

    /**
     * 갑옷 장식 재료의 효과를 관리하는 클래스.
     */
    public static final class TrimMaterialEffect extends ArmorTrimEffect<TrimMaterial> {
        /** 레지스트리 코덱 */
        public static final Codec<Holder<TrimMaterialEffect>> CODEC = VPRegistry.TRIM_MATERIAL_EFFECT.createRegistryCodec();
        /** JSON 코덱 */
        private static final Codec<TrimMaterialEffect> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
                .group(TrimMaterial.CODEC.fieldOf("material").forGetter(trimMaterialEffect -> trimMaterialEffect.holder),
                        EnchantmentEffectComponents.CODEC.optionalFieldOf("effects", DataComponentMap.EMPTY)
                                .forGetter(trimMaterialEffect -> trimMaterialEffect.enchantmentHolder.value().effects()))
                .apply(instance, TrimMaterialEffect::new));

        private TrimMaterialEffect(@NonNull Holder<TrimMaterial> trimMaterialHolder, @NonNull DataComponentMap effectMap) {
            super(trimMaterialHolder, effectMap);
        }

        /**
         * 지정한 갑옷 장식 재료에 해당하는 갑옷 장식 재료 효과를 반환한다.
         *
         * @param trimMaterialResourceKey 갑옷 장식 재료 리소스 키
         * @return 갑옷 장식 재료 효과. 존재하지 않으면 {@code null} 반환
         */
        @Nullable
        public static TrimMaterialEffect fromTrimMaterial(@NonNull ResourceKey<TrimMaterial> trimMaterialResourceKey) {
            return VPRegistry.TRIM_MATERIAL_EFFECT.getValue(trimMaterialResourceKey.location().getPath());
        }
    }

    /**
     * 갑옷 장식 형판의 효과를 관리하는 클래스.
     */
    public static final class TrimPatternEffect extends ArmorTrimEffect<TrimPattern> {
        /** 레지스트리 코덱 */
        public static final Codec<Holder<TrimPatternEffect>> CODEC = VPRegistry.TRIM_PATTERN_EFFECT.createRegistryCodec();
        /** JSON 코덱 */
        private static final Codec<TrimPatternEffect> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
                .group(TrimPattern.CODEC.fieldOf("pattern").forGetter(trimPatternEffect -> trimPatternEffect.holder),
                        EnchantmentEffectComponents.CODEC.optionalFieldOf("effects", DataComponentMap.EMPTY)
                                .forGetter(trimPatternEffect -> trimPatternEffect.enchantmentHolder.value().effects()))
                .apply(instance, TrimPatternEffect::new));

        private TrimPatternEffect(@NonNull Holder<TrimPattern> trimPatternHolder, @NonNull DataComponentMap effectMap) {
            super(trimPatternHolder, effectMap);
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
}
