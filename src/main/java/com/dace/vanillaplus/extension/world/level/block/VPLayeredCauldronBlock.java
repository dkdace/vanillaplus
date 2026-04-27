package com.dace.vanillaplus.extension.world.level.block;

import com.dace.vanillaplus.world.block.BlockModifier;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * {@link LayeredCauldronBlock}을 확장하는 인터페이스.
 *
 * @param <T> {@link BlockModifier}를 상속받는 블록 수정자
 */
public interface VPLayeredCauldronBlock<T extends BlockModifier> extends VPBlock<LayeredCauldronBlock, T>, EntityBlock {
    /** 색상 업데이트 여부 */
    BooleanProperty UPDATE_COLOR = BooleanProperty.create("update_color");
}
