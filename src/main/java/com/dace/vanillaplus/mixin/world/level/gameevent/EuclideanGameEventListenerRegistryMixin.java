package com.dace.vanillaplus.mixin.world.level.gameevent;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(EuclideanGameEventListenerRegistry.class)
public abstract class EuclideanGameEventListenerRegistryMixin implements VPMixin<EuclideanGameEventListenerRegistry> {
    @Redirect(method = "visitInRangeListeners", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/gameevent/EuclideanGameEventListenerRegistry;getPostableListenerPosition(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEventListener;)Ljava/util/Optional;"))
    private Optional<Vec3> redirectPostableListenerPosition(ServerLevel serverLevel, Vec3 pos, GameEventListener gameEventListener,
                                                            @Local(argsOnly = true) GameEvent.Context context) {
        return gameEventListener.getListenerSource().getPosition(serverLevel).map(targetPos -> {
            double range = gameEventListener.getListenerRadius();

            Entity entity = context.sourceEntity();
            if (entity instanceof LivingEntity livingEntity)
                range *= livingEntity.getAttributeValue(VPAttributes.VIBRATION_TRANSMIT_RANGE.getHolder().orElseThrow());

            double distance = BlockPos.containing(targetPos).distSqr(BlockPos.containing(pos));
            return distance > range * range ? null : targetPos;
        });
    }
}
