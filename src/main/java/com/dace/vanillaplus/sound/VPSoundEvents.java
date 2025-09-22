package com.dace.vanillaplus.sound;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.VanillaPlus;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * 모드에서 사용하는 효과음 이벤트를 관리하는 클래스.
 */
@UtilityClass
public final class VPSoundEvents {
    public static final SoundEvent RECOVERY_COMPASS_TELEPORT = create("item.recovery_compass.teleport");

    @NonNull
    private static SoundEvent create(@NonNull String name) {
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name));
        VPRegistries.SOUND_EVENT.register(name, () -> soundEvent);

        return soundEvent;
    }
}
