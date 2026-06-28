package com.dace.vanillaplus.mixin.client.renderer.entity;

import com.dace.vanillaplus.extension.client.renderer.entity.state.VPLivingEntityRenderState;
import com.dace.vanillaplus.extension.world.entity.VPLivingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState> extends EntityRendererMixin<T, S> {
    @Unique
    private static final double NAMETAG_HEIGHT = 9 * 1.15;
    @Unique
    private static final double HEALTH_OFFSET_Y = 0.5;
    @Unique
    private static final double ARMOR_OFFSET_Y = 0.75;
    @Unique
    private static final int MAX_WIDTH_ICON_COUNT = 10;
    @Unique
    private static final int ICON_INTERVAL = 8;
    @Unique
    private static final float ICON_SCALE = 0.025F;
    @Unique
    private static final int ICON_VERTEX_SIZE = 9;
    @Unique
    private static final int ICON_VERTEX_LIGHT = 0xF000F0;
    @Unique
    private static final String SPRITES_PREFIX = "textures/gui/sprites/";
    @Unique
    private static final Identifier ARMOR_SPRITE_FULL = Identifier.withDefaultNamespace("hud/armor_full");
    @Unique
    private static final Identifier ARMOR_SPRITE_HALF = Identifier.withDefaultNamespace("hud/armor_half");

    @Unique
    @NonNull
    private static <T extends LivingEntity> Gui.HeartType getHeartType(@NonNull T entity) {
        Gui.HeartType heartType;
        if (entity.hasEffect(MobEffects.POISON))
            heartType = Gui.HeartType.POISIONED;
        else if (entity.hasEffect(MobEffects.WITHER))
            heartType = Gui.HeartType.WITHERED;
        else if (entity.isFullyFrozen())
            heartType = Gui.HeartType.FROZEN;
        else
            heartType = Gui.HeartType.NORMAL;

        return heartType;
    }

    @Unique
    private void renderIcon(@NonNull S state, @NonNull PoseStack matrixStack, @NonNull SubmitNodeCollector orderedRenderCommandQueue,
                            @NonNull Identifier sprite, double height, int index, float x) {
        matrixStack.pushPose();
        matrixStack.translate(0, state.boundingBoxHeight + height, 0);

        if (state.nameTag != null)
            matrixStack.translate(0, NAMETAG_HEIGHT * ICON_SCALE, 0);

        matrixStack.mulPose(Objects.requireNonNull(entityRenderDispatcher.camera).rotation());
        matrixStack.scale(-ICON_SCALE, ICON_SCALE, ICON_SCALE);
        matrixStack.translate(0, 0, 0);

        orderedRenderCommandQueue.submitCustomGeometry(matrixStack, RenderTypes.text(sprite.withPrefix(SPRITES_PREFIX).withSuffix(".png")),
                (pose, vertexConsumer) -> {
                    Matrix4f matrix4f = pose.pose();
                    float z = (float) (Math.sqrt(state.distanceToCameraSq) + index * 0.01);

                    vertexConsumer.addVertex(matrix4f, x, -ICON_VERTEX_SIZE, z)
                            .setUv(0, 1)
                            .setLight(ICON_VERTEX_LIGHT)
                            .setColor(CommonColors.WHITE);
                    vertexConsumer.addVertex(matrix4f, x - ICON_VERTEX_SIZE, -ICON_VERTEX_SIZE, z)
                            .setUv(1, 1)
                            .setLight(ICON_VERTEX_LIGHT)
                            .setColor(CommonColors.WHITE);
                    vertexConsumer.addVertex(matrix4f, x - ICON_VERTEX_SIZE, 0, z)
                            .setUv(1, 0)
                            .setLight(ICON_VERTEX_LIGHT)
                            .setColor(CommonColors.WHITE);
                    vertexConsumer.addVertex(matrix4f, x, 0, z)
                            .setUv(0, 0)
                            .setLight(ICON_VERTEX_LIGHT)
                            .setColor(CommonColors.WHITE);
                });

        matrixStack.popPose();
    }

    @Unique
    private void submitHearts(@NonNull S state, @NonNull PoseStack matrixStack, @NonNull SubmitNodeCollector orderedRenderCommandQueue) {
        VPLivingEntityRenderState vpLivingEntityRenderState = VPLivingEntityRenderState.cast(state);
        int health = (int) Math.ceil(vpLivingEntityRenderState.getHealth());
        int healthHeartCount = (int) Math.ceil(health / 2.0);
        int maxHealth = (int) Math.ceil(vpLivingEntityRenderState.getMaxHealth());
        int maxHealthHeartCount = (int) Math.ceil(maxHealth / 2.0);
        int absorption = (int) Math.ceil(vpLivingEntityRenderState.getAbsorptionHealth());
        int absorptionHeartCount = (int) Math.ceil(absorption / 2.0);

        int totalHeartCount = maxHealthHeartCount + absorptionHeartCount;
        float interval = ICON_INTERVAL * ((float) Math.min(totalHeartCount, MAX_WIDTH_ICON_COUNT) / totalHeartCount);
        float x = (interval * maxHealthHeartCount + (ICON_INTERVAL - interval) + 1) / 2F;

        for (int i = 0; i < totalHeartCount; i++) {
            Gui.HeartType heartType;
            boolean isAbsorption = i >= maxHealthHeartCount;
            boolean isHalf;

            if (isAbsorption) {
                heartType = Gui.HeartType.ABSORBING;
                isHalf = (i - maxHealthHeartCount) * 2 + 1 == absorption;
            } else {
                heartType = vpLivingEntityRenderState.getHeartType();
                isHalf = i * 2 + 1 == health;
            }

            Identifier containerSprite = Gui.HeartType.CONTAINER.getSprite(false, false, false);
            renderIcon(state, matrixStack, orderedRenderCommandQueue, containerSprite, HEALTH_OFFSET_Y, i, x);

            if (i < healthHeartCount || isAbsorption) {
                Identifier heartSprite = heartType.getSprite(false, isHalf, false);
                renderIcon(state, matrixStack, orderedRenderCommandQueue, heartSprite, HEALTH_OFFSET_Y, i + totalHeartCount, x);
            }

            x -= interval;
        }
    }

    @Unique
    private void submitArmor(@NonNull S state, @NonNull PoseStack matrixStack, @NonNull SubmitNodeCollector orderedRenderCommandQueue) {
        int armor = VPLivingEntityRenderState.cast(state).getArmor();
        if (armor <= 0)
            return;

        int armorCount = (int) Math.ceil(armor / 2.0);
        float interval = ICON_INTERVAL * ((float) Math.min(armorCount, MAX_WIDTH_ICON_COUNT) / armorCount);
        float x = (interval * armorCount + (ICON_INTERVAL - interval) + 1) / 2F;

        for (int i = 0; i < armorCount; i++) {
            Identifier sprite = i * 2 + 1 == armor ? ARMOR_SPRITE_HALF : ARMOR_SPRITE_FULL;
            renderIcon(state, matrixStack, orderedRenderCommandQueue, sprite, ARMOR_OFFSET_Y, i, x);

            x -= interval;
        }
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("TAIL"))
    private void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        if (!VPLivingEntityRenderState.cast(state).isCanRenderHealth())
            return;

        submitHearts(state, poseStack, submitNodeCollector);
        submitArmor(state, poseStack, submitNodeCollector);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL"))
    public void extractHealth(T entity, S state, float partialTicks, CallbackInfo ci) {
        VPLivingEntityRenderState vpLivingEntityRenderState = VPLivingEntityRenderState.cast(state);
        vpLivingEntityRenderState.setHealth(entity.getHealth());
        vpLivingEntityRenderState.setMaxHealth(entity.getMaxHealth());
        vpLivingEntityRenderState.setAbsorptionHealth(entity.getAbsorptionAmount());
        vpLivingEntityRenderState.setArmor(entity.getArmorValue());
        vpLivingEntityRenderState.setCanRenderHealth(!state.isInvisibleToPlayer
                && VPLivingEntity.cast(entity).canRenderHealth(entityRenderDispatcher.crosshairPickEntity == entity));
        vpLivingEntityRenderState.setHeartType(getHeartType(entity));
    }
}
