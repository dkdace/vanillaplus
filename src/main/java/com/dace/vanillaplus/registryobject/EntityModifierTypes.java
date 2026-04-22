package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.StaticRegistry;
import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 엔티티 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
public final class EntityModifierTypes {
    static {
        DeferredRegister<MapCodec<? extends EntityModifier>> deferredRegister = StaticRegistry.ENTITY_MODIFIER_TYPE.getDeferredRegister();

        deferredRegister.register("entity", () -> EntityModifier.CODEC);
        deferredRegister.register("living_entity", () -> EntityModifier.LivingEntityModifier.CODEC);
        deferredRegister.register("ravager", () -> EntityModifier.RavagerModifier.CODEC);
        deferredRegister.register("ender_dragon", () -> EntityModifier.EnderDragonModifier.CODEC);
    }
}
