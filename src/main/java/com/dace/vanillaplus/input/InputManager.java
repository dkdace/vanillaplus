package com.dace.vanillaplus.input;

import com.dace.vanillaplus.VanillaPlus;
import com.dace.vanillaplus.extension.VPOptions;
import com.dace.vanillaplus.extension.VPPlayer;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.packet.PronePacketHandler;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * 키 입력 이벤트를 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InputManager {
    /** 엎드리기 키 */
    private static ToggleKeyMapping keyProne;

    @SubscribeEvent
    private static void onRegisterKeyMappings(@NonNull RegisterKeyMappingsEvent event) {
        VPOptions vpOptions = VPOptions.cast(Minecraft.getInstance().options);

        keyProne = new ToggleKeyMapping("key.prone", GLFW.GLFW_KEY_LEFT_ALT, KeyMapping.Category.MOVEMENT, vpOptions.getToggleProne()::get,
                true);

        event.register(keyProne);
    }

    @SubscribeEvent
    private static void onMovementInputUpdate(@NonNull MovementInputUpdateEvent event) {
        VPPlayer.cast(event.getEntity()).setProneKeyDown(keyProne.isDown());
        NetworkManager.sendToServer(new PronePacketHandler(keyProne.isDown()));
    }
}
