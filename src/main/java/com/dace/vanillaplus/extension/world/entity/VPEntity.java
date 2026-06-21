package com.dace.vanillaplus.extension.world.entity;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.world.entity.EntityConfig;
import lombok.NonNull;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.extensions.IForgeEntity;

/**
 * {@link Entity}를 확장하는 인터페이스.
 *
 * @param <T> {@link Entity}를 상속받는 타입
 * @see EntityConfig
 */
public interface VPEntity<T extends Entity> extends VPMixin<T>, IForgeEntity {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Entity> VPEntity<T> cast(@NonNull T object) {
        return (VPEntity<T>) object;
    }

    /**
     * @return 설정 데이터 요소 목록
     */
    @NonNull
    VPDataComponentMap getConfigComponents();
}
