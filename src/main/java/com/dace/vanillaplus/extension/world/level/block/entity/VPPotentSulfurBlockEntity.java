package com.dace.vanillaplus.extension.world.level.block.entity;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.data.registryobject.VPParticleTypes;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.level.block.state.properties.VPPotentSulfurState;
import lombok.NonNull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
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
    private static BlockPos findBurningNoxiousGasSourceBlock(@NonNull Level level, @NonNull BlockPos pos) {
        BlockPos.MutableBlockPos blockPos = pos.mutable();
        CollisionContext collisionContext = CollisionContext.positionContext(blockPos.getY());

        for (int i = 0; level.getFluidState(blockPos.move(Direction.UP)).is(FluidTags.LAVA); i++) {
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
    private static boolean isBurningNoxiousGasPassable(@NonNull Level level, @NonNull BlockPos originPos, @NonNull Vec3 pos) {
        return Objects.requireNonNull(level).clip(new ClipContext(Vec3.atCenterOf(originPos), pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                CollisionContext.empty())).getType() != HitResult.Type.BLOCK;
    }

    /**
     * 지정한 위치에 연소 상태의 유독 가스 입자를 재생한다.
     *
     * @param level  월드
     * @param pos    블록 위치
     * @param radius 범위
     */
    static void playBurningNoxiousGasParticles(@NonNull ClientLevel level, @NonNull BlockPos pos, double radius) {
        pos = findBurningNoxiousGasSourceBlock(level, pos);
        if (pos == null)
            return;

        Vec3 particlePos = Vec3.atCenterOf(pos).add(level.getRandom().triangle(0, radius), 0, level.getRandom().triangle(0, radius))
                .subtract(0, 0.25, 0);

        if (isBurningNoxiousGasPassable(level, pos, particlePos))
            level.addAlwaysVisibleParticle(VPParticleTypes.NOXIOUS_GAS_BURNING.get(), particlePos.x(), particlePos.y(), particlePos.z(), 0, 0,
                    0);
    }

    /**
     * 지정한 위치에 연소 상태의 유독 가스를 생성한다.
     *
     * @param level   월드
     * @param pos     블록 위치
     * @param effects 상태 효과 목록
     * @param radius  범위
     */
    static void createBurningNoxiousGas(@NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull List<MobEffectInstance> effects, double radius) {
        pos = findBurningNoxiousGasSourceBlock(level, pos);
        if (pos == null)
            return;

        AABB aabb = new AABB(pos).inflate(radius, 0, radius).expandTowards(0, radius, 0);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_SPECTATORS
                .and(EntitySelector.ENTITY_STILL_ALIVE));

        for (LivingEntity entity : entities)
            if (!entity.is(VPTags.EntityTypes.NOT_AFFECTED_BY_NOXIOUS_GAS) && isBurningNoxiousGasPassable(level, pos, entity.getEyePosition()))
                effects.forEach(mobEffectInstance -> entity.addEffect(new MobEffectInstance(mobEffectInstance)));
    }
}
