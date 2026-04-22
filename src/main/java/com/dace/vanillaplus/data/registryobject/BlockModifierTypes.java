package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.block.BlockModifier;
import lombok.experimental.UtilityClass;

/**
 * 블록 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class BlockModifierTypes {
    static {
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("block", () -> BlockModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("bell", () -> BlockModifier.BellModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("water_cauldron", () -> BlockModifier.WaterCauldronModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("cake", () -> BlockModifier.CakeModifier.CODEC);
        StaticRegistry.BLOCK_MODIFIER_TYPE.register("anvil", () -> BlockModifier.AnvilModifier.CODEC);
    }
}
