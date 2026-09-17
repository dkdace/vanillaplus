package com.dace.vanillaplus.mixin.client.particle;

import com.dace.vanillaplus.data.registryobject.VPParticleTypes;
import com.dace.vanillaplus.extension.client.particle.VPNoxiousGasCloudParticle;
import com.dace.vanillaplus.extension.world.level.block.entity.VPPotentSulfurBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.Setter;
import net.minecraft.client.particle.NoxiousGasCloudParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoxiousGasCloudParticle.class)
public abstract class NoxiousGasCloudParticleMixin extends ParticleMixin<NoxiousGasCloudParticle> implements VPNoxiousGasCloudParticle {
    @Unique
    @Setter
    private boolean isBurning;

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/PotentSulfurBlockEntity;canBeReachedByNoxiousGas(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/Vec3;)Z"), cancellable = true)
    private void addBurningNoxiousGasParticle(CallbackInfo ci, @Local(name = "sourceBlock") BlockPos sourceBlock,
                                              @Local(name = "particlePos") Vec3 particlePos) {
        if (!isBurning || !VPPotentSulfurBlockEntity.isBurningNoxiousGasPassable(level, sourceBlock, particlePos))
            return;

        level.addAlwaysVisibleParticle(VPParticleTypes.NOXIOUS_GAS_BURNING.get(), particlePos.x(), particlePos.y(), particlePos.z(), 0, 0, 0);
        ci.cancel();
    }
}
