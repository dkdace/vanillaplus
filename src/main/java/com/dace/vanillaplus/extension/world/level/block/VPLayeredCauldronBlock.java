package com.dace.vanillaplus.extension.world.level.block;

import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * {@link LayeredCauldronBlock}을 확장하는 인터페이스.
 */
public interface VPLayeredCauldronBlock extends VPBlock<LayeredCauldronBlock> {
    /** 색상 업데이트 여부 */
    BooleanProperty UPDATE_COLOR = BooleanProperty.create("update_color");
}
