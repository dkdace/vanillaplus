package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DragonSittingFlamingPhase.class)
public abstract class DragonSittingFlamingPhaseMixin extends AbstractDragonPhaseInstanceMixin {
    @ModifyExpressionValue(method = "doServerTick", at = @At(value = "CONSTANT", args = "intValue=200"))
    private int modifyFlamingDuration(int duration) {
        double flamingDurationSeconds = VPEnderDragon.cast(dragon).getDataModifier().getPhaseInfo().getSitting().getFlamingDurationSeconds()
                .get(dragon);

        return (int) (flamingDurationSeconds * 20.0);
    }

    @ModifyArg(method = "doServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadius(F)V"))
    private float modifyFlameRadius(float radius) {
        return VPEnderDragon.cast(dragon).getDataModifier().getPhaseInfo().getSitting().getFlameRadius();
    }

    @ModifyArg(method = "doServerTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/AreaEffectCloud;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private MobEffectInstance modifyFlameEffect(MobEffectInstance mobEffectInstance) {
        return VPEnderDragon.cast(dragon).getFlameMobEffectInstance();
    }
}
