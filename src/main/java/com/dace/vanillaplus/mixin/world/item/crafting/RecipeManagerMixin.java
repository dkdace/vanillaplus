package com.dace.vanillaplus.mixin.world.item.crafting;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.registryobject.VPRecipeTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipePropertySet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin implements VPMixin<RecipeManager> {
    @Mutable
    @Shadow
    @Final
    private static Map<ResourceKey<RecipePropertySet>, RecipeManager.IngredientExtractor> RECIPE_PROPERTY_SETS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addRecipePropertySets(CallbackInfo ci) {
        HashMap<ResourceKey<RecipePropertySet>, RecipeManager.IngredientExtractor> map = new HashMap<>(RECIPE_PROPERTY_SETS);

        map.put(VPRecipeTypes.Brewing.INGREDIENT_SET, recipe -> recipe instanceof VPRecipeTypes.Brewing brewing
                ? Optional.of(brewing.getIngredient())
                : Optional.empty());

        RECIPE_PROPERTY_SETS = Collections.unmodifiableMap(map);
    }
}
