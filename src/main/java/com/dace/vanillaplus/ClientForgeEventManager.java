package com.dace.vanillaplus;

import com.dace.vanillaplus.block.WaterCauldronBlockEntity;
import com.dace.vanillaplus.registryobject.VPAttributes;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 클라이언트의 {@link Mod.EventBusSubscriber.Bus#FORGE} 이벤트를 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientForgeEventManager {
    @SubscribeEvent
    private static void onViewportRenderFog(@NonNull ViewportEvent.RenderFog event) {
        if (!(event.getCamera().entity() instanceof LivingEntity livingEntity))
            return;

        float fogDistance = (float) livingEntity.getAttributeValue(VPAttributes.FOG_DISTANCE.getHolder().orElseThrow());
        event.getData().environmentalStart *= fogDistance;
        event.getData().environmentalEnd *= fogDistance;
    }

    @SubscribeEvent
    private static void onRegisterColorHandlersBlock(@NonNull RegisterColorHandlersEvent.Block event) {
        event.register(List.of(new BlockTintSource() {
            @Override
            public int color(@NonNull BlockState state) {
                return -1;
            }

            @Override
            public int colorInWorld(@NonNull BlockState state, @NonNull BlockAndTintGetter level, @NonNull BlockPos pos) {
                int averageWaterColor = BiomeColors.getAverageWaterColor(level, pos);

                return level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity
                        ? WaterCauldronBlockEntity.getMixedColor(averageWaterColor, waterCauldronBlockEntity.getColor(), 0.5F)
                        : averageWaterColor;
            }
        }), Blocks.WATER_CAULDRON);
    }
}
