package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 모드에서 사용하는 제작법 직렬화 처리기를 관리하는 클래스.
 */
@UtilityClass
public final class VPRecipeSerializers {
    static {
        create("brewing_mix", VPRecipeTypes.Brewing.Mix.SERIALIZER);
        create("brewing_transmute", VPRecipeTypes.Brewing.Transmute.SERIALIZER);
        create("brewing_mapped", VPRecipeTypes.Brewing.Mapped.SERIALIZER);
    }

    private static <T extends Recipe<?>> void create(@NonNull String name, RecipeSerializer<T> recipeSerializer) {
        VPRegistry.register(VPRegistry.RECIPE_SERIALIZER, name, () -> recipeSerializer);
    }
}
