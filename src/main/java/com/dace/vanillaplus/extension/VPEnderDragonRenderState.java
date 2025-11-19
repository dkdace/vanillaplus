package com.dace.vanillaplus.extension;

import lombok.NonNull;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * {@link EnderDragonRenderState}를 확장하는 인터페이스.
 */
public interface VPEnderDragonRenderState extends VPMixin<EnderDragonRenderState> {
    @NonNull
    static VPEnderDragonRenderState cast(@NonNull EnderDragonRenderState object) {
        return (VPEnderDragonRenderState) object;
    }

    @Nullable
    BlockPos getMeteorBeamPos();

    void setMeteorBeamPos(@Nullable BlockPos meteorBeamPos);

    float getMeteorBeamRadius();

    void setMeteorBeamRadius(float meteorBeamRadius);

    float getMeteorBeamGlowRadius();

    void setMeteorBeamGlowRadius(float meteorBeamGlowRadius);

    float getMeteorBeamAnimationTime();

    void setMeteorBeamAnimationTime(float meteorBeamAnimationTime);
}
