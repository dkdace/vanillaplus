package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.world.block.BlockModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DecoratedPotBlock.class)
public abstract class DecoratedPotBlockMixin extends BlockMixin<DecoratedPotBlock, BlockModifier> {
    @Shadow
    @Final
    public static BooleanProperty CRACKED;

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (tool.is(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING))
            blockState = blockState.setValue(CRACKED, true);

        super.playerDestroy(level, player, blockPos, blockState, blockEntity, tool);
    }
}
