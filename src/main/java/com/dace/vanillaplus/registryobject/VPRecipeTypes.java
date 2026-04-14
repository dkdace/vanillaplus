package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 모드에서 사용하는 제작법 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPRecipeTypes {
    public static final RegistryObject<RecipeType<Brewing>> BREWING = create("brewing");

    @NonNull
    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> create(@NonNull String name) {
        return VPRegistry.register(VPRegistry.RECIPE_TYPE, name, () -> RecipeType.simple(IdentifierUtil.fromPath(name)));
    }

    @NonNull
    private static ResourceKey<RecipePropertySet> createPropertySet(@NonNull String name) {
        return ResourceKey.create(RecipePropertySet.TYPE_KEY, IdentifierUtil.fromPath(name));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static abstract class Brewing implements Recipe<Brewing.Input> {
        public static final int DEFAULT_BREWING_TIME = 400;
        public static final ResourceKey<RecipePropertySet> INGREDIENT_SET = createPropertySet("brewing_ingredient");

        protected final CommonInfo commonInfo;
        @Getter
        private final int brewingTime;
        @NonNull
        @Getter
        private final Ingredient ingredient;
        @Nullable
        private PlacementInfo placementInfo;

        @NonNull
        public static HolderSet<Item> getPotionContainers() {
            return BuiltInRegistries.ITEM.getOrThrow(VPTags.Items.POTIONS);
        }

        @Override
        public boolean showNotification() {
            return commonInfo.showNotification();
        }

        @Override
        @NonNull
        public String group() {
            return "";
        }

        @Override
        @NonNull
        public abstract RecipeSerializer<? extends Brewing> getSerializer();

        @Override
        @NonNull
        public final RecipeType<Brewing> getType() {
            return BREWING.get();
        }

        @Override
        @NonNull
        public final RecipeBookCategory recipeBookCategory() {
            return VPRecipeBookCategories.BREWING.get();
        }

        @NonNull
        public Ingredient getBase() {
            return Ingredient.of(getPotionContainers());
        }

        @Override
        @MustBeInvokedByOverriders
        public boolean matches(@NonNull Input input, @NonNull Level level) {
            return getBase().test(input.base) && ingredient.test(input.ingredient);
        }

        @Override
        @NonNull
        public final PlacementInfo placementInfo() {
            if (placementInfo == null)
                placementInfo = PlacementInfo.create(List.of(getBase(), ingredient));

            return placementInfo;
        }

        public static final class Mix extends Brewing {
            private static final MapCodec<Mix> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
                    .group(CommonInfo.MAP_CODEC.forGetter(mix -> mix.commonInfo),
                            Codec.INT.optionalFieldOf("brewingtime", DEFAULT_BREWING_TIME).forGetter(Brewing::getBrewingTime),
                            Potion.CODEC.fieldOf("base").forGetter(mix -> mix.basePotion),
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(Brewing::getIngredient),
                            Potion.CODEC.fieldOf("result").forGetter(mix -> mix.resultPotion))
                    .apply(inst, Mix::new));
            private static final StreamCodec<RegistryFriendlyByteBuf, Mix> STREAM_CODEC = StreamCodec.composite(
                    CommonInfo.STREAM_CODEC, mix -> mix.commonInfo,
                    ByteBufCodecs.INT, Brewing::getBrewingTime,
                    Potion.STREAM_CODEC, mix -> mix.basePotion,
                    Ingredient.CONTENTS_STREAM_CODEC, Brewing::getIngredient,
                    Potion.STREAM_CODEC, mix -> mix.resultPotion,
                    Mix::new);
            public static final RecipeSerializer<Mix> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

            private final Holder<Potion> basePotion;
            private final Holder<Potion> resultPotion;

            private Mix(@NonNull CommonInfo commonInfo, int brewingTime, @NonNull Holder<Potion> basePotion, @NonNull Ingredient ingredient,
                        @NonNull Holder<Potion> resultPotion) {
                super(commonInfo, brewingTime, ingredient);

                this.basePotion = basePotion;
                this.resultPotion = resultPotion;
            }

            @Override
            @NonNull
            public RecipeSerializer<? extends Brewing> getSerializer() {
                return SERIALIZER;
            }

            @Override
            public boolean matches(@NonNull Input input, @NonNull Level level) {
                if (!super.matches(input, level))
                    return false;

                PotionContents potionContents = input.base.get(DataComponents.POTION_CONTENTS);
                return potionContents != null && potionContents.is(basePotion);
            }

            @Override
            @NonNull
            public ItemStack assemble(@NonNull Input input) {
                ItemStack itemStack = input.base;
                itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(resultPotion));

                return itemStack;
            }

            @Override
            @NonNull
            public List<RecipeDisplay> display() {
                return getPotionContainers().stream()
                        .map(itemHolder -> (RecipeDisplay) new VPRecipeDisplayTypes.Brewing(itemHolder.value(), basePotion,
                                super.ingredient.display(), itemHolder.value(), resultPotion))
                        .toList();
            }
        }

        public static final class Transmute extends Brewing {
            private static final MapCodec<Transmute> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
                    .group(Recipe.CommonInfo.MAP_CODEC.forGetter(transmute -> transmute.commonInfo),
                            Codec.INT.optionalFieldOf("brewingtime", DEFAULT_BREWING_TIME).forGetter(Brewing::getBrewingTime),
                            Item.CODEC.fieldOf("base").forGetter(transmute -> transmute.base),
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(Brewing::getIngredient),
                            Item.CODEC.fieldOf("result").forGetter(transmute -> transmute.result))
                    .apply(inst, Transmute::new));
            private static final StreamCodec<RegistryFriendlyByteBuf, Transmute> STREAM_CODEC = StreamCodec.composite(
                    CommonInfo.STREAM_CODEC, transmute -> transmute.commonInfo,
                    ByteBufCodecs.INT, Brewing::getBrewingTime,
                    Item.STREAM_CODEC, transmute -> transmute.base,
                    Ingredient.CONTENTS_STREAM_CODEC, Brewing::getIngredient,
                    Item.STREAM_CODEC, transmute -> transmute.result,
                    Transmute::new);
            public static final RecipeSerializer<Transmute> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

            private final Holder<Item> base;
            private final Holder<Item> result;

            private Transmute(@NonNull CommonInfo commonInfo, int brewingTime, @NonNull Holder<Item> base, @NonNull Ingredient ingredient,
                              @NonNull Holder<Item> result) {
                super(commonInfo, brewingTime, ingredient);

                this.base = base;
                this.result = result;
            }

            @Override
            @NonNull
            public RecipeSerializer<? extends Brewing> getSerializer() {
                return SERIALIZER;
            }

            @Override
            @NonNull
            public Ingredient getBase() {
                return Ingredient.of(base.value());
            }

            @Override
            @NonNull
            public ItemStack assemble(@NonNull Input input) {
                ItemStack itemStack = input.base;
                return itemStack.transmuteCopy(result.value(), itemStack.getCount());
            }

            @Override
            @NonNull
            public List<RecipeDisplay> display() {
                Registry<Potion> potionRegistry = BuiltInRegistries.POTION;

                return potionRegistry.stream()
                        .map(potion -> (RecipeDisplay) new VPRecipeDisplayTypes.Brewing(base.value(), potionRegistry.wrapAsHolder(potion),
                                super.ingredient.display(), result.value(), potionRegistry.wrapAsHolder(potion)))
                        .toList();
            }
        }

        public static final class Mapped extends Brewing {
            private static final MapCodec<Mapped> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
                    .group(CommonInfo.MAP_CODEC.forGetter(mapped -> mapped.commonInfo),
                            Codec.INT.optionalFieldOf("brewingtime", DEFAULT_BREWING_TIME).forGetter(Brewing::getBrewingTime),
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(Brewing::getIngredient),
                            Codec.unboundedMap(Potion.CODEC, Potion.CODEC).fieldOf("result_map")
                                    .forGetter(mapped -> mapped.resultPotionMap))
                    .apply(inst, Mapped::new));
            private static final StreamCodec<RegistryFriendlyByteBuf, Mapped> STREAM_CODEC = StreamCodec.composite(
                    CommonInfo.STREAM_CODEC, mapped -> mapped.commonInfo,
                    ByteBufCodecs.INT, Brewing::getBrewingTime,
                    Ingredient.CONTENTS_STREAM_CODEC, Brewing::getIngredient,
                    ByteBufCodecs.map(HashMap::new, Potion.STREAM_CODEC, Potion.STREAM_CODEC), mapped -> mapped.resultPotionMap,
                    Mapped::new);
            public static final RecipeSerializer<Mapped> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

            private final Map<Holder<Potion>, Holder<Potion>> resultPotionMap;

            private Mapped(@NonNull CommonInfo commonInfo, int brewingTime, @NonNull Ingredient ingredient, @NonNull Map<Holder<Potion>,
                    Holder<Potion>> resultPotionMap) {
                super(commonInfo, brewingTime, ingredient);
                this.resultPotionMap = resultPotionMap;
            }

            @Override
            @NonNull
            public RecipeSerializer<? extends Brewing> getSerializer() {
                return SERIALIZER;
            }

            @Override
            public boolean matches(@NonNull Input input, @NonNull Level level) {
                if (!super.matches(input, level))
                    return false;

                return getResult(input.base) != null;
            }

            @Override
            @NonNull
            public ItemStack assemble(@NonNull Input input) {
                ItemStack itemStack = getResult(input.base);
                return itemStack == null ? input.base : itemStack;
            }

            @Nullable
            public ItemStack getResult(@NonNull ItemStack itemStack) {
                PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

                return potionContents.potion().map(potionHolder -> {
                    Holder<Potion> resultPotionHolder = resultPotionMap.get(potionHolder);
                    if (resultPotionHolder == null)
                        return null;

                    ItemStack resultItemStack = itemStack.copy();
                    resultItemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(resultPotionHolder));

                    return resultItemStack;
                }).orElse(null);
            }

            @Override
            @NonNull
            public List<RecipeDisplay> display() {
                return getPotionContainers().stream()
                        .flatMap(itemHolder -> resultPotionMap.entrySet().stream().map(entry ->
                                (RecipeDisplay) new VPRecipeDisplayTypes.Brewing(itemHolder.value(), entry.getKey(), super.ingredient.display(),
                                        itemHolder.value(), entry.getValue())))
                        .toList();
            }
        }

        @AllArgsConstructor
        public static final class Input implements RecipeInput {
            private final ItemStack base;
            private final ItemStack ingredient;

            @Override
            @NonNull
            public ItemStack getItem(int index) {
                return switch (index) {
                    case 0 -> base;
                    case 1 -> ingredient;
                    default -> throw new IllegalArgumentException();
                };
            }

            @Override
            public int size() {
                return 2;
            }
        }
    }
}
