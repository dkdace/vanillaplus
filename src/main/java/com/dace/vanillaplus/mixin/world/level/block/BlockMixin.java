package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public abstract class BlockMixin<T extends Block, U extends BlockModifier> implements VPBlock<T, U> {
    @Unique
    @Nullable
    protected U dataModifier;

    @Shadow
    public abstract void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource);

    @Shadow
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity,
                              ItemStack tool) {
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;
    }
}
