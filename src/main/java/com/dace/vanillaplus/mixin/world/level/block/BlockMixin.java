package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.extension.world.level.block.VPBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public abstract class BlockMixin<T extends Block, U extends BlockModifier> implements IForgeBlock, VPBlock<T, U> {
    @Unique
    @Nullable
    protected U dataModifier;

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        this.dataModifier = dataModifier;
    }
}
