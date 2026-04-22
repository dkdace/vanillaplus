package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.StaticRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 물약을 관리하는 클래스.
 */
@UtilityClass
public final class VPPotions {
    private static final DeferredRegister<Potion> REGISTRY = StaticRegistry.createDeferredRegister(Registries.POTION);

    private static final String PREFIX_LONG = "long_";
    private static final String PREFIX_STRONG = "strong_";

    private static final String NAME_ELIXIR_OF_THE_SUN = "elixir_of_the_sun";
    public static final RegistryObject<Potion> ELIXIR_OF_THE_SUN = create(NAME_ELIXIR_OF_THE_SUN, "",
            new MobEffectInstance(MobEffects.NIGHT_VISION, 6000),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000),
            new MobEffectInstance(MobEffects.HASTE, 6000, 1));
    public static final RegistryObject<Potion> LONG_ELIXIR_OF_THE_SUN = create(NAME_ELIXIR_OF_THE_SUN, PREFIX_LONG,
            new MobEffectInstance(MobEffects.NIGHT_VISION, 14400),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 14400),
            new MobEffectInstance(MobEffects.HASTE, 14400, 1));
    public static final RegistryObject<Potion> STRONG_ELIXIR_OF_THE_SUN = create(NAME_ELIXIR_OF_THE_SUN, PREFIX_STRONG,
            new MobEffectInstance(MobEffects.NIGHT_VISION, 3600),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600),
            new MobEffectInstance(MobEffects.HASTE, 3600, 3));

    private static final String NAME_ELIXIR_OF_THE_EARTH = "elixir_of_the_earth";
    public static final RegistryObject<Potion> ELIXIR_OF_THE_EARTH = create(NAME_ELIXIR_OF_THE_EARTH, "",
            new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400),
            new MobEffectInstance(MobEffects.REGENERATION, 2400),
            new MobEffectInstance(MobEffects.RESISTANCE, 2400));
    public static final RegistryObject<Potion> LONG_ELIXIR_OF_THE_EARTH = create(NAME_ELIXIR_OF_THE_EARTH, PREFIX_LONG,
            new MobEffectInstance(MobEffects.HEALTH_BOOST, 4800),
            new MobEffectInstance(MobEffects.REGENERATION, 4800),
            new MobEffectInstance(MobEffects.RESISTANCE, 4800));
    public static final RegistryObject<Potion> STRONG_ELIXIR_OF_THE_EARTH = create(NAME_ELIXIR_OF_THE_EARTH, PREFIX_STRONG,
            new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 1),
            new MobEffectInstance(MobEffects.REGENERATION, 1200, 1),
            new MobEffectInstance(MobEffects.RESISTANCE, 1200, 1));

    @NonNull
    private static RegistryObject<Potion> create(@NonNull String name, @NonNull String prefix, @NonNull MobEffectInstance @NonNull ... mobEffectInstances) {
        return REGISTRY.register(prefix + name, () -> new Potion(name, mobEffectInstances));
    }
}
