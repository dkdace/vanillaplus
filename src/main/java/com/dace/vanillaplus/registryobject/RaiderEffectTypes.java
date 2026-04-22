package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.StaticRegistry;
import com.dace.vanillaplus.data.RaiderEffect;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 습격자 효과 타입을 관리하는 클래스.
 */
@UtilityClass
public final class RaiderEffectTypes {
    static {
        DeferredRegister<MapCodec<? extends RaiderEffect>> deferredRegister = StaticRegistry.RAIDER_EFFECT_TYPE.getDeferredRegister();

        deferredRegister.register("pillager", () -> RaiderEffect.PillagerEffect.CODEC);
        deferredRegister.register("vindicator", () -> RaiderEffect.VindicatorEffect.CODEC);
        deferredRegister.register("witch", () -> RaiderEffect.WitchEffect.CODEC);
        deferredRegister.register("ravager", () -> RaiderEffect.RavagerEffect.CODEC);
        deferredRegister.register("evoker", () -> RaiderEffect.EvokerEffect.CODEC);
        deferredRegister.register("illusioner", () -> RaiderEffect.IllusionerEffect.CODEC);
    }
}
