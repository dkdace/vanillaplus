package com.dace.vanillaplus.extension.world.level.block.entity;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.level.block.state.properties.VPPotentSulfurState;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Objects;

/**
 * {@link PotentSulfurBlockEntity}를 확장하는 인터페이스.
 */
public interface VPPotentSulfurBlockEntity extends VPMixin<PotentSulfurBlockEntity>, IForgeBlockEntity {
    /** {@link VPPotentSulfurState#BURNING}의 클라이언트 Ticker */
    MutableObject<BlockEntityTicker<PotentSulfurBlockEntity>> BURNING_CLIENT_TICKER = new MutableObject<>();
    /** {@link VPPotentSulfurState#BURNING}의 서버 Ticker */
    MutableObject<BlockEntityTicker<PotentSulfurBlockEntity>> BURNING_SERVER_TICKER = new MutableObject<>();

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
