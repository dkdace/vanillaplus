package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 물약을 관리하는 클래스.
 */
@UtilityClass
public final class VPPotions {
    private static final String NAME_ELIXIR_OF_THE_SUN = "elixir_of_the_sun";
    public static final RegistryObject<Potion> ELIXIR_OF_THE_SUN = create(NAME_ELIXIR_OF_THE_SUN, Type.NORMAL,
            new MobEffectInstance(MobEffects.NIGHT_VISION, 6000),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000),
            new MobEffectInstance(MobEffects.HASTE, 6000, 1));
    public static final RegistryObject<Potion> LONG_ELIXIR_OF_THE_SUN = create(NAME_ELIXIR_OF_THE_SUN, Type.LONG,
            new MobEffectInstance(MobEffects.NIGHT_VISION, 14400),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 14400),
            new MobEffectInstance(MobEffects.HASTE, 14400, 1));
    public static final RegistryObject<Potion> STRONG_ELIXIR_OF_THE_SUN = create(NAME_ELIXIR_OF_THE_SUN, Type.STRONG,
            new MobEffectInstance(MobEffects.NIGHT_VISION, 3600),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600),
            new MobEffectInstance(MobEffects.HASTE, 3600, 3));

    private static final String NAME_ELIXIR_OF_THE_EARTH = "elixir_of_the_earth";
    public static final RegistryObject<Potion> ELIXIR_OF_THE_EARTH = create(NAME_ELIXIR_OF_THE_EARTH, Type.NORMAL,
            new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400),
            new MobEffectInstance(MobEffects.REGENERATION, 2400),
            new MobEffectInstance(MobEffects.RESISTANCE, 2400));
    public static final RegistryObject<Potion> LONG_ELIXIR_OF_THE_EARTH = create(NAME_ELIXIR_OF_THE_EARTH, Type.LONG,
            new MobEffectInstance(MobEffects.HEALTH_BOOST, 4800),
            new MobEffectInstance(MobEffects.REGENERATION, 4800),
            new MobEffectInstance(MobEffects.RESISTANCE, 4800));
    public static final RegistryObject<Potion> STRONG_ELIXIR_OF_THE_EARTH = create(NAME_ELIXIR_OF_THE_EARTH, Type.STRONG,
            new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 1),
            new MobEffectInstance(MobEffects.REGENERATION, 1200, 1),
            new MobEffectInstance(MobEffects.RESISTANCE, 1200, 1));

    @NonNull
    private static RegistryObject<Potion> create(@NonNull String name, @NonNull Type type, @NonNull MobEffectInstance @NonNull ... mobEffectInstances) {
        return VPRegistry.POTION.register(type.prefix + name, () -> new Potion(name, mobEffectInstances));
    }

    @AllArgsConstructor
    private enum Type {
        NORMAL(""),
        LONG("long_"),
        STRONG("strong_");

        private final String prefix;
    }
}
