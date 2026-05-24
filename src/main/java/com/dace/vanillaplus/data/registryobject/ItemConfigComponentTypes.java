package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.item.CrossbowConfig;
import com.dace.vanillaplus.world.item.InstrumentConfig;
import com.dace.vanillaplus.world.item.ProjectileWeaponConfig;
import com.dace.vanillaplus.world.item.TridentConfig;
import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 아이템 설정의 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class ItemConfigComponentTypes {
    public static final RegistryObject<Codec<ProjectileWeaponConfig>> PROJECTILE_WEAPON = create(
            "projectile_weapon", () -> ProjectileWeaponConfig.CODEC);
    public static final RegistryObject<Codec<CrossbowConfig>> CROSSBOW = create(
            "crossbow", () -> CrossbowConfig.CODEC);
    public static final RegistryObject<Codec<TridentConfig>> TRIDENT = create(
            "trident", () -> TridentConfig.CODEC);
    public static final RegistryObject<Codec<InstrumentConfig>> INSTRUMENT = create(
            "instrument", () -> InstrumentConfig.CODEC);

    @NonNull
    private static <T> RegistryObject<Codec<T>> create(@NonNull String name, @NonNull Supplier<Codec<T>> onCodec) {
        return StaticRegistry.ITEM_CONFIG_COMPONENT_TYPE.register(name, () -> Codec.lazyInitialized(onCodec));
    }
}
