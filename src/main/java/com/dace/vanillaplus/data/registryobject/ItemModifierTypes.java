package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.item.ItemModifier;
import lombok.experimental.UtilityClass;

/**
 * 아이템 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class ItemModifierTypes {
    static {
        StaticRegistry.ITEM_MODIFIER_TYPE.register("item", () -> ItemModifier.CODEC);
        StaticRegistry.ITEM_MODIFIER_TYPE.register("elytra", () -> ItemModifier.ElytraModifier.CODEC);
        StaticRegistry.ITEM_MODIFIER_TYPE.register("projectile_weapon", () -> ItemModifier.ProjectileWeaponModifier.CODEC);
        StaticRegistry.ITEM_MODIFIER_TYPE.register("crossbow", () -> ItemModifier.CrossbowModifier.CODEC);
        StaticRegistry.ITEM_MODIFIER_TYPE.register("trident", () -> ItemModifier.TridentModifier.CODEC);
        StaticRegistry.ITEM_MODIFIER_TYPE.register("instrument", () -> ItemModifier.InstrumentModifier.CODEC);
    }
}
