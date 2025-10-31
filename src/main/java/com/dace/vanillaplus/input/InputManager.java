package com.dace.vanillaplus.input;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.input.key.ProneKey;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 키 입력 이벤트를 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InputManager {
    @SubscribeEvent
    private static void onRegisterKeyMappings(@NonNull RegisterKeyMappingsEvent event) {
        event.register(ProneKey.getInstance());
    }
}
