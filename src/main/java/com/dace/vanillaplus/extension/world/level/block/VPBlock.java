package com.dace.vanillaplus.extension.world.level.block;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.VPConfigurable;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.world.block.BlockConfig;
import lombok.NonNull;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeBlock;

/**
 * {@link Block}을 확장하는 인터페이스.
 *
 * @param <T> {@link Block}을 상속받는 타입
 * @see BlockConfig
 */
public interface VPBlock<T extends Block> extends VPMixin<T>, VPConfigurable<Block, BlockConfig>, IForgeBlock {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Block> VPBlock<T> cast(@NonNull T object) {
        return (VPBlock<T>) object;
    }

    /**
     * @return 설정 데이터 요소 목록
     */
    @NonNull
    VPDataComponentMap getConfigComponents();
}
