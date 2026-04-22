package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.StaticRegistry;
import com.dace.vanillaplus.item.loot.FillMap;
import com.dace.vanillaplus.item.loot.SetRandomAxolotl;
import com.dace.vanillaplus.item.loot.SetRandomFireworks;
import com.dace.vanillaplus.item.loot.SetRandomTropicalFish;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 모드에서 사용하는 전리품 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPLootFunctionTypes {
    private static final DeferredRegister<MapCodec<? extends LootItemFunction>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.LOOT_FUNCTION_TYPE);

    static {
        REGISTRY.register("set_random_tropical_fish", () -> SetRandomTropicalFish.TYPE_CODEC);
        REGISTRY.register("set_random_axolotl", () -> SetRandomAxolotl.TYPE_CODEC);
        REGISTRY.register("fill_map", () -> FillMap.TYPED_CODEC);
        REGISTRY.register("set_random_fireworks", () -> SetRandomFireworks.TYPED_CODEC);
    }
}
