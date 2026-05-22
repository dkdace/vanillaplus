package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.VPDataComponentMap;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import com.dace.vanillaplus.world.block.BlockConfig;
import lombok.NonNull;
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

import java.util.Optional;

@Mixin(Block.class)
public abstract class BlockMixin<T extends Block> implements VPBlock<T> {
    @Unique
    @Nullable
    private BlockConfig dataModifier;

    @Shadow
    public abstract void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random);

    @Shadow
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack destroyedWith) {
    }

    @Override
    @NonNull
    public final VPDataComponentMap getConfigComponents() {
        return getDataModifier().map(BlockConfig::components).orElse(VPDataComponentMap.EMPTY);
    }

    @Override
    @NonNull
    public final Optional<BlockConfig> getDataModifier() {
        return Optional.ofNullable(dataModifier);
    }

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable BlockConfig dataModifier) {
        this.dataModifier = dataModifier;
    }
}
