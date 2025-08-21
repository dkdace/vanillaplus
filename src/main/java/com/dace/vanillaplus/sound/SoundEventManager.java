package com.dace.vanillaplus.sound;

import com.dace.vanillaplus.VanillaPlus;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 효과음 이벤트를 관리하는 클래스.
 */
@UtilityClass
public final class SoundEventManager {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, VanillaPlus.MODID);

    public static final SoundEvent RECOVERY_COMPASS_TELEPORT = create("item.recovery_compass.teleport");

    @NonNull
    private static SoundEvent create(@NonNull String name) {
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name));
        SOUND_EVENTS.register(name, () -> soundEvent);

        return soundEvent;
    }
}
