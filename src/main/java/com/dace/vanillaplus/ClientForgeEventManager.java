package com.dace.vanillaplus;

import com.dace.vanillaplus.block.LayeredCauldronBlockEntity;
import com.dace.vanillaplus.registryobject.VPAttributes;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 클라이언트의 {@link Mod.EventBusSubscriber.Bus#FORGE} 이벤트를 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientForgeEventManager {
    @SubscribeEvent
    private static void onRegisterColorHandlersBlock(@NonNull RegisterColorHandlersEvent.Block event) {
        event.register((blockState, level, blockPos, index) ->
                level != null && blockPos != null && level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity
                        ? getMixedColor(BiomeColors.getAverageWaterColor(level, blockPos), layeredCauldronBlockEntity.getColor())
                        : -1, Blocks.WATER_CAULDRON);
    }

    @SubscribeEvent
    private static void onViewportRenderFog(@NonNull ViewportEvent.RenderFog event) {
        if (!(event.getCamera().getEntity() instanceof LivingEntity livingEntity))
            return;

        float fogDistance = (float) livingEntity.getAttributeValue(VPAttributes.FOG_DISTANCE.getHolder().orElseThrow());
        event.getData().environmentalStart *= fogDistance;
        event.getData().environmentalEnd *= fogDistance;
    }

    /**
     * 투명도를 기준으로 지정한 두 색상을 혼합한 색상을 반환한다.
     *
     * @param baseColor  기반 색상
     * @param addedColor 투명도가 포함된 추가 색상
     * @return 최종 색상
     */
    public static int getMixedColor(int baseColor, int addedColor) {
        float alpha = ARGB.alphaFloat(addedColor);

        float red = ARGB.redFloat(baseColor);
        red += (ARGB.redFloat(addedColor) - red) * alpha;

        float green = ARGB.greenFloat(baseColor);
        green += (ARGB.greenFloat(addedColor) - green) * alpha;

        float blue = ARGB.blueFloat(baseColor);
        blue += (ARGB.blueFloat(addedColor) - blue) * alpha;

        return ARGB.colorFromFloat(1, red, green, blue);
    }
}
