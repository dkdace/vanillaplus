package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.dace.vanillaplus.world.block.BlockModifier;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(Block.class)
public abstract class BlockMixin<T extends Block, U extends BlockModifier> implements VPBlock<T, U> {
    @Unique
    @Nullable
    private U dataModifier;

    @Shadow
    public abstract void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random);

    @Shadow
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack destroyedWith) {
    }

    @Override
    @NonNull
    public Optional<U> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return getDataModifier().map(blockModifier -> silkTouchLevel == 0 ? blockModifier.getXpRange().sample(randomSource) : 0).orElse(0);
    }
}
