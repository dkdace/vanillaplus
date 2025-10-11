package com.dace.vanillaplus.item;

import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.packet.RecoveryCompassTeleportPacketHandler;
import com.dace.vanillaplus.sound.VPSoundEvents;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 만회 나침반 아이템 클래스.
 */
public final class RecoveryCompassItem extends Item {
    public RecoveryCompassItem(@NonNull Properties properties) {
        super(properties);
    }

    /**
     * 사용 효과를 재생한다.
     *
     * @param serverLevel 월드
     * @param pos         위치
     */
    private static void playUseEffects(@NonNull ServerLevel serverLevel, @NonNull Vec3 pos) {
        serverLevel.playSound(null, pos.x, pos.y, pos.z, VPSoundEvents.RECOVERY_COMPASS_TELEPORT.get(), SoundSource.PLAYERS, 2.0F,
                1.0F);
        serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 2.0F, 0.5F);

        serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
    }

    @Override
    @NonNull
    public InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand interactionHand) {
        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResult.PASS;

        GlobalPos lastDeathPos = player.getLastDeathLocation().orElse(null);
        if (lastDeathPos == null)
            return InteractionResult.FAIL;

        Vec3 pos = getTeleportLocation(serverLevel, lastDeathPos);
        if (pos == null) {
            player.displayClientMessage(Component.translatable("item.minecraft.recovery_compass.teleport_not_valid"), true);
            return InteractionResult.FAIL;
        }

        Vec3 oldPos = player.position();

        player.teleport(new TeleportTransition(serverLevel, pos, Vec3.ZERO, 0, 0,
                Relative.union(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING));

        playUseEffects(serverLevel, oldPos);
        playUseEffects(serverLevel, pos);

        if (player instanceof ServerPlayer serverPlayer)
            NetworkManager.sendToPlayer(new RecoveryCompassTeleportPacketHandler(), serverPlayer);

        player.resetFallDistance();
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 3));

        if (player instanceof ServerPlayer)
            player.resetCurrentImpulseContext();

        player.awardStat(Stats.ITEM_USED.get(this));
        player.getItemInHand(interactionHand).consume(1, player);

        return InteractionResult.SUCCESS_SERVER;
    }

    /**
     * 이동할 위치를 반환한다.
     *
     * @param serverLevel 월드
     * @param globalPos   전역 위치
     * @return 이동할 위치. 이동할 수 없으면 {@code null} 반환
     */
    @Nullable
    private Vec3 getTeleportLocation(@NonNull ServerLevel serverLevel, @NonNull GlobalPos globalPos) {
        if (globalPos.dimension() != serverLevel.dimension())
            return null;

        BlockPos blockPos = globalPos.pos();
        while (blockPos.getY() > serverLevel.getMinY()) {
            BlockPos belowBlockPos = blockPos.below();
            if (serverLevel.getBlockState(belowBlockPos).blocksMotion())
                return blockPos.getBottomCenter();

            blockPos = belowBlockPos;
        }

        return null;
    }
}
