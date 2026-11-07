package com.dace.vanillaplus.world.block;

import com.dace.vanillaplus.data.registryobject.VPParticleTypes;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 유황 블록 클래스.
 */
public final class SulfurBlock extends Block {
    public SulfurBlock(@NonNull Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (!SulfurConfig.get().canBurn() || !(level.getBlockState(pos.above()).getBlock() instanceof BaseFireBlock) || random.nextInt(3) != 0)
            return;

        double x = pos.getX() + 0.4 + random.nextDouble() * 0.2;
        double y = pos.getY() + 1.0625;
        double z = pos.getZ() + 0.4 + random.nextDouble() * 0.2;

        level.addParticle(VPParticleTypes.NOXIOUS_GAS_BURNING.get(), x, y, z, 0, 0, 0);
    }
}
