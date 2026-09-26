package com.dace.vanillaplus.extension.world.level.block.entity;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.level.block.state.properties.VPPotentSulfurState;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

/**
 * {@link PotentSulfurBlockEntity}를 확장하는 인터페이스.
 */
public interface VPPotentSulfurBlockEntity extends VPMixin<PotentSulfurBlockEntity>, IForgeBlockEntity {
    /** 통과 가능한 최대 용암 블록 수 */
    @Unique
    int ALLOWED_LAVA_BLOCKS_ABOVE = 4;
    /** {@link VPPotentSulfurState#BURNING}의 클라이언트 Ticker */
    MutableObject<BlockEntityTicker<PotentSulfurBlockEntity>> BURNING_CLIENT_TICKER = new MutableObject<>();
    /** {@link VPPotentSulfurState#BURNING}의 서버 Ticker */
    MutableObject<BlockEntityTicker<PotentSulfurBlockEntity>> BURNING_SERVER_TICKER = new MutableObject<>();

    /**
     * 연소 상태의 유독 가스 원천 위치를 반환한다.
     *
     * @param level 월드
     * @param pos   블록 위치
     * @return 가스 원천 위치. 존재하지 않으면 {@code null} 반환
     */
    @Unique
    @Nullable
    static BlockPos findBurningNoxiousGasSourceBlock(@NonNull Level level, @NonNull BlockPos pos) {
        BlockPos.MutableBlockPos blockPos = pos.mutable();
        CollisionContext collisionContext = CollisionContext.positionContext(blockPos.getY());

        for (int i = 0; level.getBlockState(blockPos.move(Direction.UP)).is(Blocks.LAVA); i++) {
            BlockState blockState = level.getBlockState(blockPos);
            if (i > ALLOWED_LAVA_BLOCKS_ABOVE || !blockState.getCollisionShape(level, pos, collisionContext).isEmpty())
                return null;
        }

        return blockPos.immutable();
    }

    /**
     * 연소 상태의 유독 가스가 지정한 위치를 통과할 수 있는지 확인한다.
     *
     * @param level     월드
     * @param originPos 가스 원천 위치
     * @param pos       대상 위치
     * @return 통과 가능 여부
     */
    static boolean isBurningNoxiousGasPassable(@NonNull Level level, @NonNull BlockPos originPos, @NonNull Vec3 pos) {
        return Objects.requireNonNull(level).clip(new ClipContext(Vec3.atCenterOf(originPos), pos, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, CollisionContext.empty())).getType() != HitResult.Type.BLOCK;
    }
}
