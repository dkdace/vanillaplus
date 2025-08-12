package com.dace.vanillaplus.input;

import com.dace.vanillaplus.input.key.ProneKey;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

/**
 * 키 입력 이벤트를 관리하는 클래스.
 */
@UtilityClass
@OnlyIn(Dist.CLIENT)
public final class InputManager {
    public static void register(@NonNull RegisterKeyMappingsEvent event) {
        event.register(ProneKey.getInstance());
    }
}
