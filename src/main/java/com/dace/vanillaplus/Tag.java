package com.dace.vanillaplus;

import com.dace.vanillaplus.util.ReflectionUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 모드에서 사용하는 데이터 태그를 관리하는 클래스.
 */
@UtilityClass
public final class Tag {
    static {
        ReflectionUtil.loadClass(Enchantments.class);
    }

    @NonNull
    private static <T> TagKey<T> create(@NonNull ResourceKey<@NonNull Registry<T>> registry, @NonNull String name) {
        return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name));
    }

    /**
     * 마법 부여 데이터 태그.
     */
    @UtilityClass
    public static final class Enchantments {
        public static final TagKey<Enchantment> DURABILITY = create(Registries.ENCHANTMENT, "durability");
        public static final TagKey<Enchantment> AXE_TOOL = create(Registries.ENCHANTMENT, "axe_tool");
        public static final TagKey<Enchantment> AXE_WEAPON = create(Registries.ENCHANTMENT, "axe_weapon");
        public static final TagKey<Enchantment> TRADEABLE = create(Registries.ENCHANTMENT, "tradeable");
        public static final TagKey<Enchantment> TRADEABLE_TREASURE = create(Registries.ENCHANTMENT, "tradeable_treasure");
    }
}
