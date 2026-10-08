package com.dace.vanillaplus.world.item;

import com.dace.vanillaplus.data.registryobject.VPSoundEvents;
import lombok.NonNull;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 유황 가루 아이템 클래스.
 */
public final class SulfurPowderItem extends Item {
    public SulfurPowderItem(@NonNull Properties properties) {
        super(properties);
    }

    /**
     * 사용 효과를 재생한다.
     *
     * @param level      월드
     * @param blockPos   블록 위치
     * @param blockState 블록 상태
     * @param player     플레이어
     */
    private static void playUseEffects(@NonNull Level level, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @Nullable Player player) {
        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));
        level.levelEvent(player, LevelEvent.PARTICLES_SCRAPE, blockPos, 0);
        level.playSound(null, blockPos, VPSoundEvents.SULFUR_POWDER_USE.get(), SoundSource.PLAYERS, 1, 1);
    }

    @Override
    @NonNull
    public InteractionResult useOn(@NonNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();

        return WeatheringCopper.getNext(block).map(newBlock -> {
            BlockState newBlockState = newBlock.withPropertiesOf(blockState);
            Player player = context.getPlayer();

            level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL_IMMEDIATE);
            playUseEffects(level, blockPos, newBlockState, player);

            if (block instanceof ChestBlock && newBlockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                BlockPos neighborPos = ChestBlock.getConnectedBlockPos(blockPos, blockState);
                playUseEffects(level, neighborPos, level.getBlockState(neighborPos), player);
            }

            ItemStack itemInHand = context.getItemInHand();
            if (player instanceof ServerPlayer serverPlayer)
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockPos, itemInHand);

            itemInHand.shrink(1);

            return (InteractionResult) InteractionResult.SUCCESS;
        }).orElse(InteractionResult.PASS);
    }
}
