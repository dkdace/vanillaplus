package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.util.IdentifierUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 효과음 이벤트를 관리하는 클래스.
 */
@UtilityClass
public final class VPSoundEvents {
    private static final DeferredRegister<SoundEvent> REGISTRY = StaticRegistry.createDeferredRegister(Registries.SOUND_EVENT);

    public static final RegistryObject<SoundEvent> RECOVERY_COMPASS_TELEPORT = create("item.recovery_compass.teleport");
    public static final RegistryObject<SoundEvent> ENDER_DRAGON_DROP_PEARL = create("entity.ender_dragon.drop_pearl");
    public static final RegistryObject<SoundEvent> ENDER_DRAGON_SPAWN_METEOR = create("entity.ender_dragon.spawn_meteor");
    public static final RegistryObject<SoundEvent> ENDER_DRAGON_FALL_METEOR = create("entity.ender_dragon.fall_meteor");
    public static final RegistryObject<SoundEvent> ARROW_TIPPED = create("item.arrow.tipped");

    @NonNull
    private static RegistryObject<SoundEvent> create(@NonNull String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(IdentifierUtil.fromPath(name)));
    }
}
