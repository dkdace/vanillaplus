package com.dace.vanillaplus.mixin.world.entity.boss.enderdragon.phases;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import lombok.NonNull;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractDragonPhaseInstance.class)
public abstract class AbstractDragonPhaseInstanceMixin implements VPMixin<AbstractDragonPhaseInstance> {
    @Shadow
    @Final
    protected EnderDragon dragon;

    @Shadow
    public float getTurnSpeed() {
        return 0;
    }

    @Unique
    @NonNull
    protected VPEnderDragon getVPEnderDragon() {
        return VPEnderDragon.cast(dragon);
    }

    @Inject(method = "doClientTick", at = @At("TAIL"))
    protected void onClientTick(CallbackInfo ci) {
    }
}
