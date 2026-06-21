package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.world.block.BellConfig;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BellBlockEntity.class)
public abstract class BellBlockEntityMixin extends BlockEntityMixin<BellBlockEntity> {
    @ModifyArg(method = "areRaidersNearby", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"), index = 1)
    private static double modifyRaiderDetectionRange(double distance) {
        return BellConfig.get().raiderDetectionRange().map(Integer::doubleValue).orElse(distance);
    }

    @ModifyArg(method = "isRaiderWithinRange", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"), index = 1)
    private static double modifyGlowRange0(double distance) {
        return BellConfig.get().glowRange().map(Integer::doubleValue).orElse(distance);
    }

    @ModifyArg(method = "lambda$showBellParticles$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"), index = 1)
    private static double modifyGlowRange1(double distance) {
        return BellConfig.get().glowRange().map(Integer::doubleValue).orElse(distance);
    }

    @ModifyArg(method = "glow", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/core/Holder;I)V"), index = 1)
    private static int modifyGlowDuration(int duration) {
        return BellConfig.get().glowDuration().orElse(duration);
    }
}
