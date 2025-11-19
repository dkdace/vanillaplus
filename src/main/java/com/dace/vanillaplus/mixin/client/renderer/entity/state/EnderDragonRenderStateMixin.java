package com.dace.vanillaplus.mixin.client.renderer.entity.state;

import com.dace.vanillaplus.extension.VPEnderDragonRenderState;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EnderDragonRenderState.class)
@Getter
@Setter
public abstract class EnderDragonRenderStateMixin implements VPEnderDragonRenderState {
    @Unique
    @Nullable
    private BlockPos meteorBeamPos;
    @Unique
    private float meteorBeamRadius;
    @Unique
    private float meteorBeamGlowRadius;
    @Unique
    private float meteorBeamAnimationTime;
}
