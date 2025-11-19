package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingAttackingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonSittingAttackingPhase.class)
public abstract class DragonSittingAttackingPhaseMixin extends AbstractDragonPhaseInstanceMixin {
    @Shadow
    private int attackingTicks;
    @Unique
    private float yRot;
    @Unique
    private boolean spinClockwise;

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "intValue=40"))
    private int modifyRoaringDuration(int duration) {
        return ((EntityModifier.EnderDragonModifier) EntityModifier.fromEntityTypeOrThrow(dragon.getType())).getPhaseInfo().getSitting()
                .getSpinAttackDuration() + 10;
    }

    @Inject(method = "doServerTick", at = @At("TAIL"))
    private void spin(ServerLevel serverLevel, CallbackInfo ci) {
        int spinAttackDuration = ((EntityModifier.EnderDragonModifier) EntityModifier.fromEntityTypeOrThrow(dragon.getType())).getPhaseInfo()
                .getSitting().getSpinAttackDuration();

        if (attackingTicks >= spinAttackDuration)
            return;

        float angle = 360F / spinAttackDuration * 2;
        dragon.setYRot(dragon.getYRot() + (spinClockwise ? angle : -angle));
    }

    @Inject(method = "doServerTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/EnderDragonPhaseManager;setPhase(Lnet/minecraft/world/entity/boss/enderdragon/phases/EnderDragonPhase;)V"))
    private void resetYRot(ServerLevel serverLevel, CallbackInfo ci) {
        dragon.setYRot(yRot);
    }

    @Inject(method = "begin", at = @At("TAIL"))
    private void setSpinClockwise(CallbackInfo ci) {
        yRot = dragon.getYRot();
        spinClockwise = dragon.getRandom().nextBoolean();
    }
}
