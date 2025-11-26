package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VanillaPlus;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 물약을 관리하는 클래스.
 */
@UtilityClass
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
            new MobEffectInstance(MobEffects.NIGHT_VISION, 3000),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3000),
            new MobEffectInstance(MobEffects.HASTE, 3000, 3));

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

    @SubscribeEvent
    private static void onBrewingRecipeRegister(@NonNull BrewingRecipeRegisterEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        Holder<Potion> elixirOfTheSun = ELIXIR_OF_THE_SUN.getHolder().orElseThrow();

        builder.addMix(Potions.AWKWARD, Items.TORCHFLOWER, elixirOfTheSun);
        builder.addMix(elixirOfTheSun, Items.REDSTONE, LONG_ELIXIR_OF_THE_SUN.getHolder().orElseThrow());
        builder.addMix(elixirOfTheSun, Items.GLOWSTONE_DUST, STRONG_ELIXIR_OF_THE_SUN.getHolder().orElseThrow());

        Holder<Potion> elixirOfTheEarth = ELIXIR_OF_THE_EARTH.getHolder().orElseThrow();

        builder.addMix(Potions.AWKWARD, Items.PITCHER_PLANT, elixirOfTheEarth);
        builder.addMix(elixirOfTheEarth, Items.REDSTONE, LONG_ELIXIR_OF_THE_EARTH.getHolder().orElseThrow());
        builder.addMix(elixirOfTheEarth, Items.GLOWSTONE_DUST, STRONG_ELIXIR_OF_THE_EARTH.getHolder().orElseThrow());
    }

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
