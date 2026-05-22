package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.registryobject.BlockConfigComponentTypes;
import com.dace.vanillaplus.world.block.BlockConfig;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.DropExperienceBlock;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DropExperienceBlock.class)
public abstract class DropExperienceBlockMixin<T extends DropExperienceBlock> extends BlockMixin<T> {
    @Mutable
    @Shadow
    @Final
    private IntProvider xpRange;

    @Override
    @MustBeInvokedByOverriders
    public void setDataModifier(@Nullable BlockConfig dataModifier) {
        super.setDataModifier(dataModifier);
        getConfigComponents().get(BlockConfigComponentTypes.EXPERIENCE).ifPresent(experience -> xpRange = experience);
    }
}
