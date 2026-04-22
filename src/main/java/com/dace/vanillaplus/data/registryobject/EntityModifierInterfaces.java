package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.entity.EntityModifier;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.UnaryOperator;

/**
 * 엔티티의 인터페이스 수정자를 관리하는 클래스.
 */
@UtilityClass
public final class EntityModifierInterfaces {
    /** @see EntityModifier.CrossbowAttackMobInfo */
    public static final RegistryObject<DataComponentType<EntityModifier.CrossbowAttackMobInfo>> CROSSBOW_ATTACK_MOB = create(
            "crossbow_attack_mob", builder -> builder
                    .persistent(EntityModifier.CrossbowAttackMobInfo.CODEC));

    @NonNull
    private static <T> RegistryObject<DataComponentType<T>> create(@NonNull String name, @NonNull UnaryOperator<DataComponentType.Builder<T>> onBuilder) {
        return StaticRegistry.ENTITY_MODIFIER_INTERFACE.register(name, onBuilder.apply(DataComponentType.builder())::build);
    }
}
