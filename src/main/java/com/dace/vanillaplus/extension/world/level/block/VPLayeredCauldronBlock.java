package com.dace.vanillaplus.extension.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * {@link LayeredCauldronBlock}을 확장하는 인터페이스.
 */
public interface VPLayeredCauldronBlock extends VPBlock<LayeredCauldronBlock, BlockModifier>, EntityBlock {
    /** 물 투명도 */
    IntegerProperty OPACITY = IntegerProperty.create("opacity", 1, 3);
    /** 색상 업데이트 여부 */
    BooleanProperty UPDATE_COLOR = BooleanProperty.create("update_color");
}
