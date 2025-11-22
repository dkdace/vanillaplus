package com.dace.vanillaplus;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * 모드에서 사용하는 데이터 태그를 관리하는 클래스.
 */
@UtilityClass
public final class VPTags {
    @NonNull
    private static <T> TagKey<T> create(@NonNull ResourceKey<Registry<T>> registry, @NonNull String name) {
        return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath(VanillaPlus.MODID, name));
    }

    /**
     * 아이템 데이터 태그.
     */
    @UtilityClass
    public static final class Items {
        public static final TagKey<Item> NUGGETS = create(Registries.ITEM, "nuggets");
        public static final TagKey<Item> EXTENDED_ENCHANTABLE = create(Registries.ITEM, "enchantable/extended");
        public static final TagKey<Item> UNLIMITED_ENCHANTABLE = create(Registries.ITEM, "enchantable/unlimited");
    }

    /**
     * 블록 데이터 태그.
     */
    @UtilityClass
    public static final class Blocks {
        public static final TagKey<Block> DRAGON_EXPLOSION_IMMUNE = create(Registries.BLOCK, "dragon_explosion_immune");
    }

    /**
     * 마법 부여 데이터 태그.
     */
    @UtilityClass
    public static final class Enchantments {
        public static final TagKey<Enchantment> DURABILITY = create(Registries.ENCHANTMENT, "durability");
        public static final TagKey<Enchantment> TOOL = create(Registries.ENCHANTMENT, "tool");
        public static final TagKey<Enchantment> WEAPON = create(Registries.ENCHANTMENT, "weapon");
    }

    /**
     * 구조물 데이터 태그.
     */
    @UtilityClass
    public static final class Structures {
        public static final TagKey<Structure> ANCIENT_CITY = create(Registries.STRUCTURE, "ancient_city");
        public static final TagKey<Structure> ON_DESERT_EXPLORER_MAPS = create(Registries.STRUCTURE, "on_desert_explorer_maps");
        public static final TagKey<Structure> ON_SNOWY_EXPLORER_MAPS = create(Registries.STRUCTURE, "on_snowy_explorer_maps");
        public static final TagKey<Structure> PILLAGER_OUTPOST = create(Registries.STRUCTURE, "pillager_outpost");
    }

    /**
     * 피해 타입 데이터 태그.
     */
    @UtilityClass
    public static final class DamageTypes {
        public static final TagKey<DamageType> ENVIRONMENTAL = create(Registries.DAMAGE_TYPE, "environmental");
    }
}
