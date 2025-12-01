package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.VPRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 제작법 책 분류를 관리하는 클래스.
 */
@UtilityClass
public final class VPRecipeBookCategories {
    public static final RegistryObject<RecipeBookCategory> BREWING = create("brewing");

    @NonNull
    private static RegistryObject<RecipeBookCategory> create(@NonNull String name) {
        return VPRegistry.RECIPE_BOOK_CATEGORY.register(name, RecipeBookCategory::new);
    }
}
