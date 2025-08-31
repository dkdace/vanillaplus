package com.dace.vanillaplus.mixin.world.level.block;

import com.dace.vanillaplus.custom.CustomChestBlockEntity;
import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends BlockMixin implements CustomLootContainerBlock {
    @Shadow
    @UnknownNullability
    public static Direction getConnectedDirection(BlockState p_51585_) {
        return null;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/ChestBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private BlockState modifyBlockState(BlockState blockState) {
        return blockState.setValue(vp$LOOT, false).setValue(vp$ALWAYS_OPEN, false);
    }

    @ModifyVariable(method = "createBlockStateDefinition", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"),
            argsOnly = true)
    private StateDefinition.Builder<Block, BlockState> modifyBlockStateDefinition(StateDefinition.Builder<Block, BlockState> value) {
        return value.add(vp$LOOT, vp$ALWAYS_OPEN);
    }

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"))
    protected void onOpen(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult,
                          CallbackInfoReturnable<InteractionResult> cir) {
        vp$popOpenXP(blockState, level, blockPos);

        if (blockState.getValue(ChestBlock.TYPE) == ChestType.SINGLE)
            return;

        BlockPos connectedBlockPos = blockPos.relative(getConnectedDirection(blockState));
        vp$popOpenXP(level.getBlockState(connectedBlockPos), level, connectedBlockPos);
    }

    @Unique
    private void vp$popOpenXP(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel) || !blockState.getValue(vp$LOOT) || blockState.getValue(vp$ALWAYS_OPEN)
                || !(level.getBlockEntity(blockPos) instanceof CustomChestBlockEntity customChestBlockEntity))
            return;

        level.setBlockAndUpdate(blockPos, blockState.setValue(vp$ALWAYS_OPEN, true));
        popExperience(serverLevel, blockPos, customChestBlockEntity.vp$getXp());
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        if (state.getValue(vp$LOOT) && !state.getValue(vp$ALWAYS_OPEN)
                && level.getBlockEntity(pos) instanceof CustomChestBlockEntity customChestBlockEntity)
            return customChestBlockEntity.vp$getXp();

        return 0;
    }
}
