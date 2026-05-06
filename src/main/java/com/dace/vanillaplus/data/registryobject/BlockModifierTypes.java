package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.block.modifier.*;
import lombok.experimental.UtilityClass;

/**
 * 블록 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class BlockModifierTypes {
    static {
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("block", () -> BlockModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("bell", () -> BellBlockModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("water_cauldron", () -> WaterCauldronBlockModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("cake", () -> CakeBlockModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("anvil", () -> AnvilBlockModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("brewing_stand", () -> BrewingStandBlockModifier.CODEC);
    }
}
