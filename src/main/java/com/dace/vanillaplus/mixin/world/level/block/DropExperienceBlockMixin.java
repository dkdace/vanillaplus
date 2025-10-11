package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.rebalance.modifier.BlockModifier;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.DropExperienceBlock;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DropExperienceBlock.class)
public abstract class DropExperienceBlockMixin extends BlockMixin<BlockModifier.DropExperienceModifier> {
    @Mutable
    @Shadow
    @Final
    private IntProvider xpRange;

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable BlockModifier.DropExperienceModifier dataModifier) {
        super.setDataModifier(dataModifier);

        if (dataModifier != null)
            xpRange = dataModifier.getXpRange();
    }
}
