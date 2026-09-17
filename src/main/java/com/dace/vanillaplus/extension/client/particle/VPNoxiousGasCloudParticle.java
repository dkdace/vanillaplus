package com.dace.vanillaplus.extension.client.particle;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.client.particle.NoxiousGasCloudParticle;

/**
 * {@link NoxiousGasCloudParticle}을 확장하는 인터페이스.
 */
public interface VPNoxiousGasCloudParticle extends VPMixin<NoxiousGasCloudParticle> {
    @NonNull
    static VPNoxiousGasCloudParticle cast(@NonNull NoxiousGasCloudParticle object) {
        return (VPNoxiousGasCloudParticle) object;
    }

    /**
     * @param isBurning 연소 여부
     */
    void setBurning(boolean isBurning);
}
