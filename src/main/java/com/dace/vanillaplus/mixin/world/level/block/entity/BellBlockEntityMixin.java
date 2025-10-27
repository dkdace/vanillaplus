package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.data.modifier.BlockModifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BellBlockEntity.class)
public abstract class BellBlockEntityMixin extends BlockEntityMixin<BellBlockEntity> {
    @ModifyArg(method = "areRaidersNearby", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"), index = 1)
    private static double modifyRaiderDetectionRange(double range) {
        return ((BlockModifier.BellModifier) BlockModifier.fromBlockOrThrow(Blocks.BELL)).getRaiderDetectionRange();
    }

    @ModifyArg(method = "isRaiderWithinRange", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"), index = 1)
    private static double modifyGlowRange0(double range) {
        return ((BlockModifier.BellModifier) BlockModifier.fromBlockOrThrow(Blocks.BELL)).getGlowRange();
    }

    @ModifyArg(method = "lambda$showBellParticles$1", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"), index = 1)
    private static double modifyGlowRange1(double range) {
        return ((BlockModifier.BellModifier) BlockModifier.fromBlockOrThrow(Blocks.BELL)).getGlowRange();
    }

    @ModifyArg(method = "glow", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/core/Holder;I)V"), index = 1)
    private static int modifyGlowDuration(int duration) {
        return ((BlockModifier.BellModifier) BlockModifier.fromBlockOrThrow(Blocks.BELL)).getGlowDuration();
    }
}
