package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.data.registryobject.VPParticleTypes;
import com.dace.vanillaplus.extension.world.level.block.entity.VPPotentSulfurBlockEntity;
import com.dace.vanillaplus.world.block.PotentSulfurConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PotentSulfurBlock;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PotentSulfurState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotentSulfurBlockEntity.class)
public abstract class PotentSulfurBlockEntityMixin extends BlockEntityMixin<PotentSulfurBlockEntity> implements VPPotentSulfurBlockEntity {
    @Unique
    private static final double BURNING_NOXIOUS_GAS_RADIUS = 2.5;
    @Unique
    private static final int ALLOWED_LAVA_BLOCKS_ABOVE = 4;

    @Unique
    @Nullable
    private static BlockPos findPassableBlock(@NonNull Level level, @NonNull BlockPos pos) {
        BlockPos.MutableBlockPos blockPos = pos.mutable();
        CollisionContext collisionContext = CollisionContext.positionContext(blockPos.getY());

        for (int i = 0; level.getBlockState(blockPos.move(Direction.UP)).is(Blocks.LAVA); i++) {
            BlockState blockState = level.getBlockState(blockPos);
            if (i > ALLOWED_LAVA_BLOCKS_ABOVE || !blockState.getCollisionShape(level, pos, collisionContext).isEmpty())
                return null;
        }

        return blockPos.immutable();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        BURNING_CLIENT_TICKER.setValue((level, pos, _, _) -> {
            if (level.getGameTime() % 10 != 0)
                return;

            BlockPos blockPos = findPassableBlock(level, pos);
            if (blockPos == null)
                return;

            Vec3 center = Vec3.atCenterOf(blockPos);
            level.addParticle(VPParticleTypes.NOXIOUS_GAS_CLOUD_BURNING.get(), center.x(), center.y(), center.z(), 0, 0, 0);
        });
        BURNING_SERVER_TICKER.setValue((level, pos, state, _) -> {
            if (!PotentSulfurConfig.get().canBurn()) {
                level.setBlockAndUpdate(pos, state.setValue(PotentSulfurBlock.STATE, PotentSulfurState.DRY));
                return;
            }
            if (level.getGameTime() % 10 != 0)
                return;

            BlockPos blockPos = findPassableBlock(level, pos);
            if (blockPos == null)
                return;

            AABB aabb = new AABB(blockPos).inflate(BURNING_NOXIOUS_GAS_RADIUS, 0, BURNING_NOXIOUS_GAS_RADIUS)
                    .expandTowards(0, BURNING_NOXIOUS_GAS_RADIUS, 0);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_SPECTATORS
                    .and(EntitySelector.ENTITY_STILL_ALIVE));

            for (LivingEntity entity : entities)
                if (VPPotentSulfurBlockEntity.isBurningNoxiousGasPassable(level, blockPos, entity.getEyePosition()))
                    PotentSulfurConfig.get().burningNoxiousGasEffects().forEach(mobEffectInstance ->
                            entity.addEffect(new MobEffectInstance(mobEffectInstance)));
        });
    }

    @WrapOperation(method = "applyNauseaEffect", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static boolean modifyNoxiousGasEffect(LivingEntity instance, MobEffectInstance newEffect, Operation<Boolean> original) {
        return PotentSulfurConfig.get().overrideNoxiousGasEffects().map(override -> override || original.call(instance, newEffect),
                mobEffectInstances -> {
                    mobEffectInstances.forEach(mobEffectInstance -> instance.addEffect(new MobEffectInstance(mobEffectInstance)));
                    return true;
                });
    }
}
