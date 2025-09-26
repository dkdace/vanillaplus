package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.rebalance.modifier.BlockModifier;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public abstract class BlockMixin<T extends BlockModifier> implements IForgeBlock, VPModifiableData<Block, T> {
    @Unique
    @Nullable
    @Getter
    protected T dataModifier;

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@NonNull T dataModifier) {
        this.dataModifier = dataModifier;
    }
}
