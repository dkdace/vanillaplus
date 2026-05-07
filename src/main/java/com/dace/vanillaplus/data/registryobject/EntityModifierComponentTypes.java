package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.entity.modifier.component.CrossbowMobInfo;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 엔티티 수정자의 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class EntityModifierComponentTypes {
    public static final RegistryObject<Codec<CrossbowMobInfo>> CROSSBOW_ATTACK_MOB = create("crossbow_mob", () ->
            CrossbowMobInfo.CODEC);

    @NonNull
    private static <T> RegistryObject<Codec<T>> create(@NonNull String name, @NonNull Supplier<Codec<T>> onCodec) {
        return StaticRegistry.ENTITY_MODIFIER_COMPONENT_TYPE.register(name, onCodec);
    }
}
