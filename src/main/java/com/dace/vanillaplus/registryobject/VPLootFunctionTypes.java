package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.mojang.serialization.MapCodec;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

/**
 * 모드에서 사용하는 전리품 수정자 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPLootFunctionTypes {
    static {
        VPRegistry.LOOT_FUNCTION_TYPE.register("set_random_tropical_fish", () -> SetRandomTropicalFish.CODEC);
        VPRegistry.LOOT_FUNCTION_TYPE.register("set_random_axolotl", () -> SetRandomAxolotl.CODEC);
        VPRegistry.LOOT_FUNCTION_TYPE.register("fill_map", () -> FillMap.CODEC);
    }

    @NoArgsConstructor
    private static final class SetRandomTropicalFish implements LootItemFunction {
        private static final SetRandomTropicalFish instance = new SetRandomTropicalFish();
        private static final MapCodec<SetRandomTropicalFish> CODEC = MapCodec.unit(instance);

        @Override
        @NonNull
        public MapCodec<? extends LootItemFunction> codec() {
            return CODEC;
        }

        @Override
        public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
            List<TropicalFish.Variant> variants = TropicalFish.COMMON_VARIANTS;
            TropicalFish.Variant variant = variants.get(lootContext.getRandom().nextInt(variants.size()));

            itemStack.set(DataComponents.TROPICAL_FISH_PATTERN, variant.pattern());
            itemStack.set(DataComponents.TROPICAL_FISH_BASE_COLOR, variant.baseColor());
            itemStack.set(DataComponents.TROPICAL_FISH_PATTERN_COLOR, variant.patternColor());

            return itemStack;
        }
    }

    @NoArgsConstructor
    private static final class SetRandomAxolotl implements LootItemFunction {
        private static final SetRandomAxolotl instance = new SetRandomAxolotl();
        private static final MapCodec<SetRandomAxolotl> CODEC = MapCodec.unit(instance);

        @Override
        @NonNull
        public MapCodec<? extends LootItemFunction> codec() {
            return CODEC;
        }

        @Override
        public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
            itemStack.set(DataComponents.AXOLOTL_VARIANT, Axolotl.Variant.getCommonSpawnVariant(lootContext.getRandom()));
            return itemStack;
        }
    }

    @NoArgsConstructor
    private static final class FillMap implements LootItemFunction {
        private static final FillMap instance = new FillMap();
        private static final MapCodec<FillMap> CODEC = MapCodec.unit(instance);

        @Override
        @NonNull
        public MapCodec<? extends LootItemFunction> codec() {
            return CODEC;
        }

        @Override
        public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
            BlockPos blockPos = BlockPos.containing(lootContext.getParameter(LootContextParams.ORIGIN));
            return MapItem.create(lootContext.getLevel(), blockPos.getX(), blockPos.getZ(), (byte) 0, true, false);
        }
    }
}
