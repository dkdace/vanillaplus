package com.dace.vanillaplus;

import com.dace.vanillaplus.client.renderer.LayeredCauldronRenderer;
import com.dace.vanillaplus.registryobject.VPAttributes;
import com.dace.vanillaplus.registryobject.VPBlockEntityTypes;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
    private static void onViewportRenderFog(@NonNull ViewportEvent.RenderFog event) {
        if (!(event.getCamera().entity() instanceof LivingEntity livingEntity))
            return;

        float fogDistance = (float) livingEntity.getAttributeValue(VPAttributes.FOG_DISTANCE.getHolder().orElseThrow());
        event.getData().environmentalStart *= fogDistance;
        event.getData().environmentalEnd *= fogDistance;
    }

    @SubscribeEvent
    private static void onEntityRenderersRegisterRenderers(@NonNull EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VPBlockEntityTypes.LAYERED_CAULDRON.get(), context -> new LayeredCauldronRenderer());
    }
}
