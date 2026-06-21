package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.item.loot.BadOmenLevelProvider;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 모드에서 사용하는 숫자 제공자 타입을 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class VPLootNumberProviderTypes {
    private static final DeferredRegister<MapCodec<? extends NumberProvider>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.LOOT_NUMBER_PROVIDER_TYPE);

    static {
        REGISTRY.register("bad_omen_level", () -> BadOmenLevelProvider.TYPED_CODEC);
    }
}
