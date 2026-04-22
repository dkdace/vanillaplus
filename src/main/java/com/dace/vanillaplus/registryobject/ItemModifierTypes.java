package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.StaticRegistry;
import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 아이템 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
public final class ItemModifierTypes {
    static {
        DeferredRegister<MapCodec<? extends ItemModifier>> deferredRegister = StaticRegistry.ITEM_MODIFIER_TYPE.getDeferredRegister();

        deferredRegister.register("item", () -> ItemModifier.CODEC);
        deferredRegister.register("elytra", () -> ItemModifier.ElytraModifier.CODEC);
        deferredRegister.register("projectile_weapon", () -> ItemModifier.ProjectileWeaponModifier.CODEC);
        deferredRegister.register("crossbow", () -> ItemModifier.CrossbowModifier.CODEC);
        deferredRegister.register("trident", () -> ItemModifier.TridentModifier.CODEC);
        deferredRegister.register("instrument", () -> ItemModifier.InstrumentModifier.CODEC);
    }
}
