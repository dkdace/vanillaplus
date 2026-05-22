package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.util.CodecUtil;
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

import java.util.Optional;

/**
 * 블록 설정의 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class BlockConfigComponentTypes {
    private static int currentId = 0;

    public static final RegistryObject<VPDataComponentMap.Key<Optional<IntProvider>>> EXPERIENCE = create(
            "experience", CodecUtil.optional(IntProviders.NON_NEGATIVE_CODEC), Optional.empty());
    public static final RegistryObject<VPDataComponentMap.Key<Optional<FoodProperties>>> FOOD = create(
            "food", CodecUtil.optional(FoodProperties.DIRECT_CODEC), Optional.empty());
    public static final RegistryObject<VPDataComponentMap.Key<BellConfig>> BELL = create(
            "bell", BellConfig.CODEC, BellConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<AnvilConfig>> ANVIL = create(
            "anvil", AnvilConfig.CODEC, AnvilConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<BrewingStandConfig>> BREWING_STAND = create(
            "brewing_stand", BrewingStandConfig.CODEC, BrewingStandConfig.DEFAULT);
    public static final RegistryObject<VPDataComponentMap.Key<WaterCauldronConfig>> WATER_CAULDRON = create(
            "water_cauldron", WaterCauldronConfig.CODEC, WaterCauldronConfig.DEFAULT);

    @NonNull
    private static <T> RegistryObject<VPDataComponentMap.Key<T>> create(@NonNull String name, @NonNull Codec<T> codec, @NonNull T defaultValue) {
        return StaticRegistry.BLOCK_CONFIG_COMPONENT_TYPE.register(name, () -> new VPDataComponentMap.Key<>(currentId++, codec, defaultValue));
    }
}
