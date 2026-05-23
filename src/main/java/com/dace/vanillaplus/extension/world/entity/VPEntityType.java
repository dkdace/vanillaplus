package com.dace.vanillaplus.extension.world.entity;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.entity.EntityConfig;
import lombok.NonNull;
import net.minecraft.world.entity.EntityType;

/**
 * {@link EntityType}을 확장하는 인터페이스.
 */
public interface VPEntityType extends VPMixin<EntityType<?>>, VPModifiableData<EntityType<?>, EntityConfig> {
    @NonNull
    static VPEntityType cast(@NonNull EntityType<?> object) {
        return (VPEntityType) object;
    }

    /**
     * @return 설정 데이터 요소 목록
     */
    @NonNull
    VPDataComponentMap getConfigComponents();
}
