package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.block.AnvilConfig;
import com.dace.vanillaplus.world.block.BellConfig;
import com.dace.vanillaplus.world.block.BrewingStandConfig;
import com.dace.vanillaplus.world.block.WaterCauldronConfig;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 블록 설정의 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class BlockConfigComponentTypes {
    public static final RegistryObject<Codec<IntProvider>> EXPERIENCE = create(
            "experience", () -> IntProviders.NON_NEGATIVE_CODEC);
    public static final RegistryObject<Codec<FoodProperties>> FOOD = create(
            "food", () -> FoodProperties.DIRECT_CODEC);
    public static final RegistryObject<Codec<BellConfig>> BELL = create(
            "bell", () -> BellConfig.CODEC);
    public static final RegistryObject<Codec<AnvilConfig>> ANVIL = create(
            "anvil", () -> AnvilConfig.CODEC);
    public static final RegistryObject<Codec<BrewingStandConfig>> BREWING_STAND = create(
            "brewing_stand", () -> BrewingStandConfig.CODEC);
    public static final RegistryObject<Codec<WaterCauldronConfig>> WATER_CAULDRON = create(
            "water_cauldron", () -> WaterCauldronConfig.CODEC);

    @NonNull
    private static <T> RegistryObject<Codec<T>> create(@NonNull String name, @NonNull Supplier<Codec<T>> onCodec) {
        return StaticRegistry.BLOCK_CONFIG_COMPONENT_TYPE.register(name, () -> Codec.lazyInitialized(onCodec));
    }
}
