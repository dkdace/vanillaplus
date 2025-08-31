package com.dace.vanillaplus.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin implements IForgeBlock {
    @Shadow
    public abstract void popExperience(ServerLevel serverLevel, BlockPos blockPos, int xp);
}
