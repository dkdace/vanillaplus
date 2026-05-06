package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 블록 수정자의 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class BlockModifierComponentTypes {
    public static final RegistryObject<Codec<IntProvider>> EXPERIENCE = create("experience", () ->
            IntProviders.NON_NEGATIVE_CODEC);

    @NonNull
    private static <T> RegistryObject<Codec<T>> create(@NonNull String name, @NonNull Supplier<Codec<T>> onCodec) {
        return StaticRegistry.BLOCK_MODIFIER_COMPONENT_TYPE.register(name, onCodec);
    }
}
