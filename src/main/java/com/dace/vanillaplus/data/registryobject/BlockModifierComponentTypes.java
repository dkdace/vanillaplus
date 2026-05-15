package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.util.CodecUtil;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

/**
 * 블록 수정자의 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class BlockModifierComponentTypes {
    private static int currentId = 0;

    public static final RegistryObject<VPDataComponentMap.Key<Optional<IntProvider>>> EXPERIENCE = create("experience",
            CodecUtil.optional(IntProviders.NON_NEGATIVE_CODEC), Optional.empty());

    @NonNull
    private static <T> RegistryObject<VPDataComponentMap.Key<T>> create(@NonNull String name, @NonNull Codec<T> codec, @NonNull T defaultValue) {
        return StaticRegistry.BLOCK_MODIFIER_COMPONENT_TYPE.register(name, () -> new VPDataComponentMap.Key<>(currentId++, codec, defaultValue));
    }
}
