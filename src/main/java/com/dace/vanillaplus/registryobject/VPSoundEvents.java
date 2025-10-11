package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.VanillaPlus;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 효과음 이벤트를 관리하는 클래스.
 */
@UtilityClass
public final class VPSoundEvents {
    public static final RegistryObject<SoundEvent> RECOVERY_COMPASS_TELEPORT = create("item.recovery_compass.teleport");

    @NonNull
    private static RegistryObject<SoundEvent> create(@NonNull String name) {
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name));
        return VPRegistries.SOUND_EVENT.register(name, () -> soundEvent);
    }
}
