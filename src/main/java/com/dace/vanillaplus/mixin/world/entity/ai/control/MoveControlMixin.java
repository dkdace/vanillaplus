package com.dace.vanillaplus.mixin.world.entity.ai.control;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.entity.VPMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MoveControl.class)
public abstract class MoveControlMixin implements VPMixin<MoveControl> {
    @Shadow
    @Final
    protected Mob mob;
    @Shadow
    protected MoveControl.Operation operation;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void jumpIfCannotReachTarget(CallbackInfo ci) {
        if (!VPMob.cast(mob).getDataModifier().canJumpAtTarget())
            return;

        LivingEntity target = mob.getTarget();
        if (target == null)
            return;

        double yDiff = target.getY() - mob.getY();
        double height = mob.getBbHeight();

        if (!mob.onGround() || yDiff <= height || yDiff >= height + 2
                || !mob.position().horizontal().closerThan(target.position().horizontal(), mob.getBbWidth() + 1.0))
            return;

        mob.getJumpControl().jump();
        operation = MoveControl.Operation.JUMPING;
    }
}
