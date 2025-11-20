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
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonRenderer.class)
public abstract class EnderDragonRendererMixin implements VPMixin<EnderDragonRenderer> {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"))
    private void submitMeteorBeam(EnderDragonRenderState enderDragonRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                  CameraRenderState cameraRenderState, CallbackInfo ci) {
        VPEnderDragonRenderState vpEnderDragonRenderState = VPEnderDragonRenderState.cast(enderDragonRenderState);
        BlockPos meteorPos = vpEnderDragonRenderState.getMeteorBeamPos();
        if (meteorPos == null)
            return;

        PoseStack meteorPoseStack = new PoseStack();
        meteorPoseStack.pushPose();

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        meteorPoseStack.translate(meteorPos.getX() - cameraPos.x(), meteorPos.getY() - cameraPos.y(), meteorPos.getZ() - cameraPos.z());

        BeaconRenderer.submitBeaconBeam(meteorPoseStack, submitNodeCollector, BeaconRenderer.BEAM_LOCATION, 1,
                vpEnderDragonRenderState.getMeteorBeamAnimationTime(), -1024, 2048, DyeColor.PURPLE.getTextureDiffuseColor(),
                vpEnderDragonRenderState.getMeteorBeamRadius(), vpEnderDragonRenderState.getMeteorBeamGlowRadius());

        meteorPoseStack.popPose();
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;F)V",
            at = @At("TAIL"))
    private void extractMeteorBeam(EnderDragon enderDragon, EnderDragonRenderState enderDragonRenderState, float partialTick, CallbackInfo ci) {
        VPEnderDragonRenderState vpEnderDragonRenderState = VPEnderDragonRenderState.cast(enderDragonRenderState);
        BlockPos meteorPos = VPEnderDragon.cast(enderDragon).getMeteorPos();

        vpEnderDragonRenderState.setMeteorBeamPos(meteorPos);
        if (meteorPos == null)
            return;

        vpEnderDragonRenderState.setMeteorBeamAnimationTime(Math.floorMod(enderDragon.level().getGameTime(), 80) + partialTick);

        float height = (float) meteorPos.getY() / enderDragon.level().getMaxY();
        vpEnderDragonRenderState.setMeteorBeamRadius(Mth.lerp(height, 0, 2));
        vpEnderDragonRenderState.setMeteorBeamGlowRadius(Mth.lerp(height, 0, 4));
    }
}
