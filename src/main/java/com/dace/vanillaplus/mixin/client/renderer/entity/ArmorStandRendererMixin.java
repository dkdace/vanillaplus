package com.dace.vanillaplus.mixin.client.renderer.entity;

import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin extends LivingEntityRendererMixin<ArmorStand, ArmorStandRenderState> {
    @Override
    protected boolean canRenderHealth(@NonNull ArmorStand entity, @NonNull ArmorStandRenderState state) {
        return false;
    }
}
