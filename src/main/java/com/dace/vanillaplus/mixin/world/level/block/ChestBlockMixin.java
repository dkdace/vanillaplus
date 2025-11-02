package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.extension.VPLootContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin<T extends ChestBlock, U extends BlockModifier> extends BlockMixin<T, U> implements VPLootContainerBlock<T> {
    @Shadow
    @UnknownNullability
    public static Direction getConnectedDirection(BlockState blockState) {
        return null;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/ChestBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private BlockState modifyBlockState(BlockState blockState) {
        return blockState.setValue(LOOT, false).setValue(ALWAYS_OPEN, false);
    }

    @ModifyVariable(method = "createBlockStateDefinition", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"),
            argsOnly = true)
    private StateDefinition.Builder<Block, BlockState> modifyBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        return builder.add(LOOT, ALWAYS_OPEN);
    }

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"))
    protected void onOpen(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult,
                          CallbackInfoReturnable<InteractionResult> cir) {
        popOpenXP(blockState, level, blockPos);

        if (blockState.getValue(ChestBlock.TYPE) == ChestType.SINGLE)
            return;

        BlockPos connectedBlockPos = blockPos.relative(getConnectedDirection(blockState));
        popOpenXP(level.getBlockState(connectedBlockPos), level, connectedBlockPos);
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return getXp(state, level, randomSource, pos);
    }
}
