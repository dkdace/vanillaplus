package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.item.component.ExtraFood;
import com.dace.vanillaplus.world.item.component.RepairWithXP;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.UnaryOperator;

/**
 * 모드에서 사용하는 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPDataComponentTypes {
    private static final DeferredRegister<DataComponentType<?>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.DATA_COMPONENT_TYPE);

    public static final RegistryObject<DataComponentType<Integer>> REPAIR_LIMIT = create(
            "repair_limit", builder -> builder
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final RegistryObject<DataComponentType<Holder<TrimPattern>>> PROVIDES_TRIM_PATTERN = create(
            "provides_trim_pattern", builder -> builder
                    .persistent(TrimPattern.CODEC)
                    .networkSynchronized(TrimPattern.STREAM_CODEC)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<Integer>> ENCHANTMENT_LEVEL_MULTIPLIER = create(
            "enchantment_level_multiplier", builder -> builder
                    .persistent(ExtraCodecs.POSITIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<Float>> SMELTING_DAMAGE_RATIO = create(
            "smelting_damage_ratio", builder -> builder
                    .persistent(ExtraCodecs.floatRange(0, 1))
                    .networkSynchronized(ByteBufCodecs.FLOAT)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<RepairWithXP>> REPAIR_WITH_XP = create(
            "repair_with_xp", builder -> builder
                    .persistent(RepairWithXP.CODEC)
                    .networkSynchronized(RepairWithXP.STREAM_CODEC)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<Long>> SEED = create(
            "seed", builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG));
    public static final RegistryObject<DataComponentType<ExtraFood>> EXTRA_FOOD = create(
            "extra_food", builder -> builder
                    .persistent(ExtraFood.CODEC)
                    .networkSynchronized(ExtraFood.STREAM_CODEC)
                    .cacheEncoding());

    @NonNull
    private static <T> RegistryObject<DataComponentType<T>> create(@NonNull String name, @NonNull UnaryOperator<DataComponentType.Builder<T>> onBuilder) {
        return REGISTRY.register(name, onBuilder.apply(DataComponentType.builder())::build);
    }
}
