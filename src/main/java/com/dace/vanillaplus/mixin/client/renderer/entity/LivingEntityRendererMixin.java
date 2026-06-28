package com.dace.vanillaplus.mixin.client.renderer.entity;

import com.dace.vanillaplus.extension.client.gui.VPGui;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
    private static float getIconInterval(int count) {
        return ICON_INTERVAL * ((float) Math.min(count, MAX_WIDTH_ICON_COUNT) / count);
    }

    @Unique
    private static float getIconStartPosition(float interval, int count) {
        return (interval * count + (ICON_INTERVAL - interval) + 1) / 2F;
    }

    @Unique
    @NonNull
    private static <T extends LivingEntity> Gui.HeartType getHeartType(@NonNull T entity) {
        VPLivingEntity<T> vpLivingEntity = VPLivingEntity.cast(entity);

        if (vpLivingEntity.isPoisoned())
            return Gui.HeartType.POISIONED;
        if (vpLivingEntity.isWithered())
            return Gui.HeartType.WITHERED;
        if (entity.isFullyFrozen())
            return Gui.HeartType.FROZEN;

        return Gui.HeartType.NORMAL;
    }

    @Unique
    protected boolean canRenderHealth(@NonNull T entity, @NonNull S state) {
        return !state.isInvisibleToPlayer && (VPLivingEntity.cast(entity).canRenderHealth() || entityRenderDispatcher.crosshairPickEntity == entity
                || state.appearsGlowing());
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
        float interval = getIconInterval(totalHeartCount);
        float x = getIconStartPosition(interval, totalHeartCount);

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
        int armorToughness = VPLivingEntityRenderState.cast(state).getArmorToughness();
        if (armor <= 0 && armorToughness <= 0)
            return;

        int armorCount = (int) Math.ceil(armor / 2.0);
        int armorToughnessCount = (int) Math.ceil(armorToughness / 2.0);

        int maxArmorCount = Math.max(armorCount, armorToughnessCount);
        float interval = getIconInterval(maxArmorCount);
        float x = getIconStartPosition(interval, maxArmorCount);

        for (int i = 0; i < maxArmorCount; i++) {
            int value = i * 2 + 1;

            Identifier armorSprite = null;
            if (value == armor)
                armorSprite = ARMOR_SPRITE_HALF;
            else if (value < armor)
                armorSprite = ARMOR_SPRITE_FULL;

            Identifier armorToughnessSprite = null;
            if (value == armorToughness)
                armorToughnessSprite = VPGui.ARMOR_TOUGHNESS_HALF_SPRITE;
            else if (value < armorToughness)
                armorToughnessSprite = VPGui.ARMOR_TOUGHNESS_FULL_SPRITE;

            if (armorSprite != null)
                renderIcon(state, matrixStack, orderedRenderCommandQueue, armorSprite, ARMOR_OFFSET_Y, i, x);
            if (armorToughnessSprite != null)
                renderIcon(state, matrixStack, orderedRenderCommandQueue, armorToughnessSprite, ARMOR_OFFSET_Y, i + armorCount, x);

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
        vpLivingEntityRenderState.setArmorToughness((int) Math.floor(entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS)));
        vpLivingEntityRenderState.setCanRenderHealth(canRenderHealth(entity, state));
        vpLivingEntityRenderState.setHeartType(getHeartType(entity));
    }
}
