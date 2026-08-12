package com.dace.vanillaplus.mixin.client.renderer.entity;

import com.dace.vanillaplus.extension.VPMixin;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> implements VPMixin<EntityRenderer<T, S>> {
    @Shadow
    @Final
    protected EntityRenderDispatcher entityRenderDispatcher;
}
