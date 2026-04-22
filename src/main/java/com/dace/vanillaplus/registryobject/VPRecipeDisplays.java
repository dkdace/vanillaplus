package com.dace.vanillaplus.registryobject;

import com.dace.vanillaplus.StaticRegistry;
import com.dace.vanillaplus.item.crafting.display.BrewingRecipeDisplay;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraftforge.registries.DeferredRegister;

/**
 * 모드에서 사용하는 제작법 디스플레이를 관리하는 클래스.
 */
@UtilityClass
public final class VPRecipeDisplays {
    private static final DeferredRegister<RecipeDisplay.Type<?>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.RECIPE_DISPLAY);

    static {
        REGISTRY.register("brewing", () -> BrewingRecipeDisplay.DISPLAY_TYPE);
    }
}
