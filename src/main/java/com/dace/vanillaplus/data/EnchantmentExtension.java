package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * 마법 부여 확장을 관리하는 클래스.
 */
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantmentExtension {
    /** 레지스트리 코덱 */
    public static final Codec<Holder<EnchantmentExtension>> CODEC = VPRegistry.ENCHANTMENT_EXTENSION.createRegistryCodec();
    /** JSON 코덱 */
    private static final Codec<EnchantmentExtension> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Enchantment.CODEC.fieldOf("enchantment")
                            .forGetter(enchantmentExtension -> enchantmentExtension.enchantmentHolder),
                    Codec.unboundedMap(Codec.STRING, DefinedValue.CODEC).optionalFieldOf("values", Collections.emptyMap())
                            .forGetter(enchantmentExtension -> enchantmentExtension.definedValueMap),
                    ExtraCodecs.intRange(0, 255).optionalFieldOf("extended_max_level", 0)
                            .forGetter(EnchantmentExtension::getExtendedMaxLevel))
            .apply(instance, EnchantmentExtension::new));

    /** 마법 부여 홀더 인스턴스 */
    private final Holder<Enchantment> enchantmentHolder;
    /** 이름별 사전 정의된 값 목록 (이름 : 사전 정의된 값) */
    private final TreeMap<String, DefinedValue> definedValueMap;
    /** 확장된 최대 마법 부여 레벨 */
    @Getter
    private final int extendedMaxLevel;

    private EnchantmentExtension(@NonNull Holder<Enchantment> enchantmentHolder, @NonNull Map<String, DefinedValue> definedValueMap,
                                 int extendedMaxLevel) {
        this.enchantmentHolder = enchantmentHolder;
        this.definedValueMap = new TreeMap<>(Comparator.comparing(k -> definedValueMap.get(k).getDescriptionIndex()));
        this.definedValueMap.putAll(definedValueMap);
        this.extendedMaxLevel = extendedMaxLevel;
    }

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.ENCHANTMENT_EXTENSION.getRegistryKey(), DIRECT_CODEC, DIRECT_CODEC);
    }

    /**
     * 지정한 마법 부여에 해당하는 마법 부여 확장을 반환한다.
     *
     * @param enchantmentResourceKey 마법 부여 리소스 키
     * @return 마법 부여 확장. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    public static EnchantmentExtension fromEnchantment(@NonNull ResourceKey<Enchantment> enchantmentResourceKey) {
        return VPRegistry.ENCHANTMENT_EXTENSION.getValue(enchantmentResourceKey.location().getPath());
    }

    /**
     * @return 사전 정의된 값 목록
     */
    @NonNull
    @UnmodifiableView
    public Collection<DefinedValue> getValues() {
        return Collections.unmodifiableCollection(definedValueMap.values());
    }

    /**
     * 지정한 이름에 해당하는 사전 정의된 값을 반환한다.
     *
     * @param name 이름
     * @return 사전 정의된 값
     * @throws NullPointerException 해당하는 사전 정의된 값이 존재하지 않으면 발생
     */
    @NonNull
    public DefinedValue getValue(@NonNull String name) {
        return Objects.requireNonNull(definedValueMap.get(name));
    }

    /**
     * 지정한 아이템의 최대 마법 부여 레벨을 반환한다.
     *
     * @param itemStack 대상 아이템
     * @return 최대 마법 부여 레벨
     */
    public int getMaxLevel(@NonNull ItemStack itemStack) {
        int originalMaxLevel = enchantmentHolder.value().getMaxLevel();
        return extendedMaxLevel > originalMaxLevel && itemStack.is(VPTags.Items.UNLIMITED_ENCHANTABLE) ? extendedMaxLevel : originalMaxLevel;
    }

    /**
     * 사전 정의된 값 클래스.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class DefinedValue {
        /** JSON 코덱 */
        private static final Codec<DefinedValue> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("description_index").forGetter(DefinedValue::getDescriptionIndex),
                        Codec.FLOAT.optionalFieldOf("description_value_multiplier", 1F)
                                .forGetter(DefinedValue::getDescriptionValueMultiplier),
                        LevelBasedValue.CODEC.fieldOf("value").forGetter(DefinedValue::getLevelBasedValue))
                .apply(instance, DefinedValue::new));

        /** 설명 포맷 인덱스 */
        private final int descriptionIndex;
        /** 설명 표시 값에 적용되는 배수 */
        private final float descriptionValueMultiplier;
        /** 레벨 기반 값 */
        @NonNull
        private final LevelBasedValue levelBasedValue;
    }
}
