package com.dace.vanillaplus.extension.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import lombok.NonNull;
import net.minecraft.world.level.block.Block;

/**
 * {@link Block}을 확장하는 인터페이스.
 *
 * @param <T> {@link Block}을 상속받는 타입
 * @param <U> {@link BlockModifier}를 상속받는 블록 수정자
 * @see BlockModifier
 */
public interface VPBlock<T extends Block, U extends BlockModifier> extends VPMixin<T>, VPModifiableData<Block, U> {
    @NonNull
    @SuppressWarnings("unchecked")
    static <T extends Block, U extends BlockModifier> VPBlock<T, U> cast(@NonNull T object) {
        return (VPBlock<T, U>) object;
    }
}
