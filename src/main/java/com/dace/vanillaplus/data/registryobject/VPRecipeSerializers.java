package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import com.dace.vanillaplus.world.item.crafting.BrewingRecipe;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 모드에서 사용하는 제작법 직렬화 처리기를 관리하는 클래스.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class VPRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.RECIPE_SERIALIZER);

    static {
        REGISTRY.register("brewing_mix", () -> BrewingRecipe.Mix.SERIALIZER);
        REGISTRY.register("brewing_transmute", () -> BrewingRecipe.Transmute.SERIALIZER);
        REGISTRY.register("brewing_mapped", () -> BrewingRecipe.Mapped.SERIALIZER);
    }
}
