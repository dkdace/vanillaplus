package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.StaticRegistry;
import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 블록 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
public final class BlockModifierTypes {
    static {
        DeferredRegister<MapCodec<? extends BlockModifier>> deferredRegister = StaticRegistry.BLOCK_MODIFIER_TYPE.getDeferredRegister();

        deferredRegister.register("block", () -> BlockModifier.CODEC);
        deferredRegister.register("bell", () -> BlockModifier.BellModifier.CODEC);
        deferredRegister.register("water_cauldron", () -> BlockModifier.WaterCauldronModifier.CODEC);
        deferredRegister.register("cake", () -> BlockModifier.CakeModifier.CODEC);
        deferredRegister.register("anvil", () -> BlockModifier.AnvilModifier.CODEC);
    }
}
