package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistries;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.UnaryOperator;

/**
 * 모드에서 사용하는 데이터 요소 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPDataComponentTypes {
    public static final RegistryObject<DataComponentType<Integer>> REPAIR_LIMIT = create("repair_limit", builder ->
            builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final RegistryObject<DataComponentType<Integer>> MAX_REPAIR_LIMIT = create("max_repair_limit", builder ->
            builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    @NonNull
    @SuppressWarnings("unchecked")
    private static <T> RegistryObject<DataComponentType<T>> create(@NonNull String name, @NonNull UnaryOperator<DataComponentType.Builder<T>> onBuilder) {
        return (RegistryObject<DataComponentType<T>>) (Object) VPRegistries.DATA_COMPONENT_TYPE.register(name,
                onBuilder.apply(DataComponentType.builder())::build);
    }
}
