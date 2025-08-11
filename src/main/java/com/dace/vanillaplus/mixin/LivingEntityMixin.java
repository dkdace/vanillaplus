package com.dace.vanillaplus.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyArg(method = "hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hasLineOfSight(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;D)Z"),
            index = 1)
    private ClipContext.Block modifyLineOfSightClipContextBlock(ClipContext.Block block) {
        return ClipContext.Block.VISUAL;
    }

    @Inject(method = "die", at = @At("TAIL"))
    protected void onDie(DamageSource damageSource, CallbackInfo ci) {
        // 미사용
    }
}
