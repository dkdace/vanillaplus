package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.util.IdentifierUtil;
import com.dace.vanillaplus.world.item.crafting.BrewingRecipe;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 제작법 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.RECIPE_TYPE);

    public static final RegistryObject<RecipeType<BrewingRecipe>> BREWING = create("brewing");

    @NonNull
    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> create(@NonNull String name) {
        return REGISTRY.register(name, () -> RecipeType.simple(IdentifierUtil.fromPath(name)));
    }

    @NonNull
    public static ResourceKey<RecipePropertySet> createPropertySet(@NonNull String name) {
        return ResourceKey.create(RecipePropertySet.TYPE_KEY, IdentifierUtil.fromPath(name));
    }
}
