package com.dace.vanillaplus.mixin.client.renderer.entity;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.client.renderer.entity.state.VPEnderDragonRenderState;
import com.dace.vanillaplus.extension.world.entity.boss.enderdragon.VPEnderDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonRenderer.class)
public abstract class EnderDragonRendererMixin implements VPMixin<EnderDragonRenderer> {
    @Unique
    private static final int METEOR_BEAM_ANIMATION_TIME = 80;
    @Unique
    private static final float METEOR_BEAM_MAX_RADIUS = 2;
    @Unique
    private static final float METEOR_BEAM_MAX_GLOW_RADIUS = 4;

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"))
    private void submitMeteorBeam(EnderDragonRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera,
                                  CallbackInfo ci) {
        VPEnderDragonRenderState vpEnderDragonRenderState = VPEnderDragonRenderState.cast(state);
        BlockPos meteorPos = vpEnderDragonRenderState.getMeteorBeamPos();
        if (meteorPos == null)
            return;

        PoseStack meteorPoseStack = new PoseStack();
        meteorPoseStack.pushPose();

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        meteorPoseStack.translate(meteorPos.getX() - cameraPos.x(), meteorPos.getY() - cameraPos.y(), meteorPos.getZ() - cameraPos.z());

        int height = BeaconRenderer.MAX_RENDER_Y;

        BeaconRenderer.submitBeaconBeam(meteorPoseStack, submitNodeCollector, BeaconRenderer.BEAM_LOCATION, 1,
                vpEnderDragonRenderState.getMeteorBeamAnimationTime(), -height / 2, height, DyeColor.PURPLE.getTextureDiffuseColor(),
                vpEnderDragonRenderState.getMeteorBeamRadius(), vpEnderDragonRenderState.getMeteorBeamGlowRadius());

        meteorPoseStack.popPose();
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;F)V",
            at = @At("TAIL"))
    private void extractMeteorBeam(EnderDragon entity, EnderDragonRenderState state, float partialTicks, CallbackInfo ci) {
        VPEnderDragonRenderState vpEnderDragonRenderState = VPEnderDragonRenderState.cast(state);
        BlockPos meteorPos = VPEnderDragon.cast(entity).getMeteorPos();

        vpEnderDragonRenderState.setMeteorBeamPos(meteorPos);
        if (meteorPos == null)
            return;

        vpEnderDragonRenderState.setMeteorBeamAnimationTime(Math.floorMod(entity.level().getGameTime(), METEOR_BEAM_ANIMATION_TIME) + partialTicks);

        float height = (float) meteorPos.getY() / entity.level().getMaxY();
        vpEnderDragonRenderState.setMeteorBeamRadius(Mth.lerp(height, 0, METEOR_BEAM_MAX_RADIUS));
        vpEnderDragonRenderState.setMeteorBeamGlowRadius(Mth.lerp(height, 0, METEOR_BEAM_MAX_GLOW_RADIUS));
    }
}
