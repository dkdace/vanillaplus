package com.dace.vanillaplus;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 클라이언트 이벤트를 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientManager {
    @SubscribeEvent
    private static void onFMLClientSetup(@NonNull FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(Blocks.WATER_CAULDRON, ChunkSectionLayer.TRANSLUCENT);
    }
}
