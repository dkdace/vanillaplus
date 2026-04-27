package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.entity.raid.RaiderEffect;
import lombok.experimental.UtilityClass;

/**
 * 습격자 효과 타입을 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class RaiderEffectTypes {
    static {
        StaticRegistry.RAIDER_EFFECT_TYPE.register("pillager", () -> RaiderEffect.PillagerEffect.CODEC);
        StaticRegistry.RAIDER_EFFECT_TYPE.register("vindicator", () -> RaiderEffect.VindicatorEffect.CODEC);
        StaticRegistry.RAIDER_EFFECT_TYPE.register("witch", () -> RaiderEffect.WitchEffect.CODEC);
        StaticRegistry.RAIDER_EFFECT_TYPE.register("ravager", () -> RaiderEffect.RavagerEffect.CODEC);
        StaticRegistry.RAIDER_EFFECT_TYPE.register("evoker", () -> RaiderEffect.EvokerEffect.CODEC);
        StaticRegistry.RAIDER_EFFECT_TYPE.register("illusioner", () -> RaiderEffect.IllusionerEffect.CODEC);
    }
}
