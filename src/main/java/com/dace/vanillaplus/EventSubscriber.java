package com.dace.vanillaplus;

import com.dace.vanillaplus.input.InputManager;
import com.dace.vanillaplus.network.NetworkManager;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * 이벤트 등록 시 관련 이벤트 처리기를 등록하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EventSubscriber {
    @SubscribeEvent
    private static void onRegisterKeyMappings(@NonNull RegisterKeyMappingsEvent event) {
        InputManager.register(event);
    }

    @SubscribeEvent
    private static void onFMLCommonSetup(@NonNull FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkManager::register);
    }
}
