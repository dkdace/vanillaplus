package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.DropExperienceBlock;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DropExperienceBlock.class)
public abstract class DropExperienceBlockMixin<T extends DropExperienceBlock, U extends BlockModifier.DropExperienceModifier> extends BlockMixin<T, U> {
    @Mutable
    @Shadow
    @Final
    private IntProvider xpRange;

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable U dataModifier) {
        super.setDataModifier(dataModifier);

        if (dataModifier != null)
            xpRange = dataModifier.getXpRange();
    }
}
