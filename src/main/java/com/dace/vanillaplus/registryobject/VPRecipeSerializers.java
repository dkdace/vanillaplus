package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 제작법 직렬화 처리기를 관리하는 클래스.
 */
@UtilityClass
public final class VPRecipeSerializers {
    public static final RegistryObject<RecipeSerializer<VPRecipeTypes.Brewing.Mix>> BREWING_MIX = create("brewing_mix",
            new VPRecipeTypes.Brewing.Mix.Serializer());
    public static final RegistryObject<RecipeSerializer<VPRecipeTypes.Brewing.Transmute>> BREWING_TRANSMUTE = create("brewing_transmute",
            new VPRecipeTypes.Brewing.Transmute.Serializer());
    public static final RegistryObject<RecipeSerializer<VPRecipeTypes.Brewing.Mapped>> BREWING_MAPPED = create("brewing_mapped",
            new VPRecipeTypes.Brewing.Mapped.Serializer());

    @NonNull
    private static <T extends Recipe<?>> RegistryObject<RecipeSerializer<T>> create(@NonNull String name, RecipeSerializer<T> recipeSerializer) {
        return VPRegistry.register(VPRegistry.RECIPE_SERIALIZER, name, () -> recipeSerializer);
    }
}
