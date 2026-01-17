package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

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
    @Unique
    private static final int SPIN_TIMES = 2;

    @Shadow
    private int attackingTicks;
    @Unique
    private float yRot;
    @Unique
    private boolean isSpinClockwise;

    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "intValue=40"))
    private int modifyRoaringDuration(int duration) {
        return getVPEnderDragon().getDataModifier()
                .map(enderDragonModifier -> enderDragonModifier.getPhaseInfo().getSitting().getSpinAttackDuration() + 10)
                .orElse(duration);
    }

    @Inject(method = "doServerTick", at = @At("TAIL"))
    private void spin(ServerLevel serverLevel, CallbackInfo ci) {
        getVPEnderDragon().getDataModifier().ifPresent(enderDragonModifier -> {
            int spinAttackDuration = enderDragonModifier.getPhaseInfo().getSitting().getSpinAttackDuration();

            if (attackingTicks >= spinAttackDuration)
                return;

            float angle = 360F / spinAttackDuration * SPIN_TIMES;
            dragon.setYRot(dragon.getYRot() + (isSpinClockwise ? angle : -angle));
        });
    }

    @Inject(method = "doServerTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/EnderDragonPhaseManager;setPhase(Lnet/minecraft/world/entity/boss/enderdragon/phases/EnderDragonPhase;)V"))
    private void resetYRot(ServerLevel serverLevel, CallbackInfo ci) {
        if (getVPEnderDragon().getDataModifier().isPresent())
            dragon.setYRot(yRot);
    }

    @Inject(method = "begin", at = @At("TAIL"))
    private void setSpinClockwise(CallbackInfo ci) {
        yRot = dragon.getYRot();
        isSpinClockwise = dragon.getRandom().nextBoolean();
    }
}
