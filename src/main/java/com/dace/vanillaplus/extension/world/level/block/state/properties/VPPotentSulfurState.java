package com.dace.vanillaplus.extension.world.level.block.state.properties;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.world.level.block.state.properties.PotentSulfurState;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * {@link PotentSulfurState}를 확장하는 인터페이스.
 */
public interface VPPotentSulfurState extends VPMixin<PotentSulfurState> {
    /** 연소 */
    MutableObject<PotentSulfurState> BURNING = new MutableObject<>();
}
