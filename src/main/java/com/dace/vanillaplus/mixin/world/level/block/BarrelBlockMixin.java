package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.custom.CustomBarrelBlockEntity;
import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BarrelBlock.class)
public abstract class BarrelBlockMixin extends BlockMixin implements CustomLootContainerBlock {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/BarrelBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private BlockState modifyBlockState(BlockState blockState) {
        return blockState.setValue(LOOT, false).setValue(ALWAYS_OPEN, false);
    }

    @ModifyVariable(method = "createBlockStateDefinition", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"),
            argsOnly = true)
    private StateDefinition.Builder<Block, BlockState> modifyBlockStateDefinition(StateDefinition.Builder<Block, BlockState> value) {
        return value.add(LOOT, ALWAYS_OPEN);
    }

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"))
    protected void popOpenXP(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult,
                             CallbackInfoReturnable<InteractionResult> cir, @Local ServerLevel serverLevel) {
        if (!blockState.getValue(LOOT) || blockState.getValue(ALWAYS_OPEN)
                || !(level.getBlockEntity(blockPos) instanceof CustomBarrelBlockEntity customBarrelBlockEntity))
            return;

        level.setBlockAndUpdate(blockPos, blockState.setValue(ALWAYS_OPEN, true));
        popExperience(serverLevel, blockPos, customBarrelBlockEntity.getXp());
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        if (state.getValue(LOOT) && !state.getValue(ALWAYS_OPEN)
                && level.getBlockEntity(pos) instanceof CustomBarrelBlockEntity customBarrelBlockEntity)
            return customBarrelBlockEntity.getXp();

        return 0;
    }
}
