package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.custom.CustomModifiableData;
import com.dace.vanillaplus.rebalance.modifier.BlockModifier;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin implements IForgeBlock, CustomModifiableData<Block, BlockModifier> {
    @Shadow
    public abstract void popExperience(ServerLevel serverLevel, BlockPos blockPos, int xp);

    @Override
    public void apply(@NonNull BlockModifier modifier) {
        // 미사용
    }
}
