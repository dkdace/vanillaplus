package com.dace.vanillaplus.client.renderer;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.block.WaterCauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * {@link WaterCauldronBlockEntity}의 렌더러 클래스.
 */
public final class WaterCauldronRenderer implements BlockEntityRenderer<WaterCauldronBlockEntity, WaterCauldronRenderer.RenderState> {
    /** 물 텍스쳐 식별자 */
    private static final Identifier RESOURCE_LOCATION = VanillaPlus.createIdentifier("water_still_opaque");
    /** 물의 최소 투명도 */
    private static final float MIN_ALPHA = 0.5F;

    /** 물 텍스쳐 스프라이트 */
    private final TextureAtlasSprite sprite;

    public WaterCauldronRenderer() {
        this.sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS)
                .getSprite(Sheets.BLOCKS_MAPPER.apply(RESOURCE_LOCATION).texture());
    }

    private static void addVertex(@NonNull RenderState renderState, @NonNull PoseStack.Pose pose, @NonNull VertexConsumer vertexConsumer, float x,
                                  float z, float u, float v) {
        vertexConsumer.addVertex(pose, x, 0, z)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(renderState.lightCoords)
                .setNormal(pose, 0, 1, 0)
                .setColor(renderState.color);
    }

    /**
     * 투명도를 기준으로 색상을 혼합한다.
     *
     * @param baseColor  기반 색상
     * @param addedColor 투명도가 포함된 추가 색상
     * @param minAlpha   최소 투명도
     * @return 최종 색상
     */
    public static int getMixedColor(int baseColor, int addedColor, float minAlpha) {
        float alpha = ARGB.alphaFloat(addedColor);

        float red = ARGB.redFloat(baseColor);
        red += (ARGB.redFloat(addedColor) - red) * alpha;

        float green = ARGB.greenFloat(baseColor);
        green += (ARGB.greenFloat(addedColor) - green) * alpha;

        float blue = ARGB.blueFloat(baseColor);
        blue += (ARGB.blueFloat(addedColor) - blue) * alpha;

        return ARGB.colorFromFloat(Mth.clampedLerp(minAlpha, 1, alpha), red, green, blue);
    }

    @Override
    @NonNull
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(@NonNull WaterCauldronBlockEntity waterCauldronBlockEntity, @NonNull RenderState renderState, float partialTick,
                                   @NonNull Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(waterCauldronBlockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

        Level level = waterCauldronBlockEntity.getLevel();
        if (level == null)
            return;

        renderState.color = getMixedColor(BiomeColors.getAverageWaterColor(level, renderState.blockPos), waterCauldronBlockEntity.getColor(),
                MIN_ALPHA);
        renderState.height = ((LayeredCauldronBlock) renderState.blockState.getBlock()).getContentHeight(renderState.blockState);
    }

    @Override
    public void submit(@NonNull RenderState renderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector,
                       @NonNull CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, renderState.height, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, vertexConsumer) -> {
            addVertex(renderState, pose, vertexConsumer, -0.5F, -0.5F, sprite.getU1(), sprite.getV1());
            addVertex(renderState, pose, vertexConsumer, 0.5F, -0.5F, sprite.getU0(), sprite.getV1());
            addVertex(renderState, pose, vertexConsumer, 0.5F, 0.5F, sprite.getU0(), sprite.getV0());
            addVertex(renderState, pose, vertexConsumer, -0.5F, 0.5F, sprite.getU1(), sprite.getV0());
        });

        poseStack.popPose();
    }

    /**
     * 렌더링 상태 클래스.
     */
    public static final class RenderState extends BlockEntityRenderState {
        /** 색상 */
        private int color = 0;
        /** 높이 */
        private double height;
    }
}
