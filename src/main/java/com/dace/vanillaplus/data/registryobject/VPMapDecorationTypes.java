package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.util.IdentifierUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 지도 장식 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPMapDecorationTypes {
    private static final DeferredRegister<MapDecorationType> REGISTRY = StaticRegistry.createDeferredRegister(Registries.MAP_DECORATION_TYPE);

    public static final RegistryObject<MapDecorationType> MINESHAFT = create("mineshaft", ARGB.color(144, 142, 127));
    public static final RegistryObject<MapDecorationType> PILLAGER_OUTPOST = create("pillager_outpost", ARGB.color(122, 99, 67));
    public static final RegistryObject<MapDecorationType> TRAIL_RUINS = create("trail_ruins", ARGB.color(210, 158, 152));
    public static final RegistryObject<MapDecorationType> ANCIENT_CITY = create("ancient_city", ARGB.color(29, 56, 62));
    public static final RegistryObject<MapDecorationType> DESERT_PYRAMID = create("desert_pyramid", ARGB.color(217, 212, 63));
    public static final RegistryObject<MapDecorationType> IGLOO = create("igloo", ARGB.color(134, 216, 244));

    @NonNull
    private static RegistryObject<MapDecorationType> create(@NonNull String name, int mapColor) {
        return REGISTRY.register(name, () -> new MapDecorationType(IdentifierUtil.fromPath(name), true, mapColor,
                true, false));
    }
}
