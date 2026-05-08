package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.entity.modifier.*;
import lombok.experimental.UtilityClass;

/**
 * 엔티티 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class EntityModifierTypes {
    static {
        StaticRegistry.ENTITY_MODIFIER_TYPE.register("entity", () -> EntityModifier.CODEC);
        StaticRegistry.ENTITY_MODIFIER_TYPE.register("living_entity", () -> LivingEntityModifier.CODEC);
        StaticRegistry.ENTITY_MODIFIER_TYPE.register("mob", () -> MobModifier.CODEC);
        StaticRegistry.ENTITY_MODIFIER_TYPE.register("ravager", () -> RavagerModifier.CODEC);
        StaticRegistry.ENTITY_MODIFIER_TYPE.register("ender_dragon", () -> EnderDragonModifier.CODEC);
    }
}
