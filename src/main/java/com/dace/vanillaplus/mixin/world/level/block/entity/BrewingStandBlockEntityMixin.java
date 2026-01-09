package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.extension.world.level.block.entity.VPBrewingStandBlockEntity;
import com.dace.vanillaplus.registryobject.VPRecipeTypes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin extends BlockEntityMixin<BrewingStandBlockEntity> implements VPBrewingStandBlockEntity {
    @Shadow
    @Final
    public static final int NUM_DATA_VALUES = VPBrewingStandBlockEntity.NUM_DATA_VALUES;
    @Unique
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
    @Shadow
    @Final
    private static int INGREDIENT_SLOT;

    @Unique
    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
    @Unique
    @Getter
    private RecipeManager.CachedCheck<VPRecipeTypes.Brewing.Input, ? extends VPRecipeTypes.Brewing> quickCheck;
    @Unique
    @Getter
    @Setter
    private int totalBrewTime;
    @Shadow
    private NonNullList<ItemStack> items;

    @Unique
    @Nullable
    private static RecipeHolder<? extends VPRecipeTypes.Brewing> getRecipeResult(@NonNull BrewingStandBlockEntity brewingStandBlockEntity,
                                                                                 @NonNull VPRecipeTypes.Brewing.Input input, @NonNull Level level) {
        return VPBrewingStandBlockEntity.cast(brewingStandBlockEntity).getQuickCheck().getRecipeFor(input, (ServerLevel) level).orElse(null);
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;isBrewable(Lnet/minecraft/world/item/alchemy/PotionBrewing;Lnet/minecraft/core/NonNullList;)Z"))
    private static boolean redirectIsBrewable(PotionBrewing potionBrewing, NonNullList<ItemStack> itemStacks, @Local(argsOnly = true) Level level,
                                              @Local(argsOnly = true) BrewingStandBlockEntity brewingStandBlockEntity) {
        ItemStack ingredient = itemStacks.get(INGREDIENT_SLOT);

        if (!ingredient.isEmpty())
            for (int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; i++) {
                ItemStack potion = itemStacks.get(i);
                VPRecipeTypes.Brewing.Input input = new VPRecipeTypes.Brewing.Input(potion, ingredient);

                if (!potion.isEmpty() && getRecipeResult(brewingStandBlockEntity, input, level) != null)
                    return true;
            }

        return false;
    }

    @ModifyExpressionValue(method = "serverTick", at = @At(value = "CONSTANT", args = "intValue=400"))
    private static int modifyBrewingDuration(int duration, @Local(argsOnly = true) Level level,
                                             @Local(argsOnly = true) BrewingStandBlockEntity brewingStandBlockEntity) {
        for (int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; i++) {
            ItemStack potion = brewingStandBlockEntity.getItem(i);
            VPRecipeTypes.Brewing.Input input = new VPRecipeTypes.Brewing.Input(potion, brewingStandBlockEntity.getItem(INGREDIENT_SLOT));

            RecipeHolder<? extends VPRecipeTypes.Brewing> recipeHolder = getRecipeResult(brewingStandBlockEntity, input, level);
            if (recipeHolder != null)
                duration = Math.max(duration, recipeHolder.value().getBrewingTime());
        }

        VPBrewingStandBlockEntity.cast(brewingStandBlockEntity).setTotalBrewTime(duration);
        return duration;
    }

    @Redirect(method = "doBrew", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionBrewing;mix(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack redirectResult(PotionBrewing instance, ItemStack ingredient, ItemStack potion, @Local(argsOnly = true) Level level,
                                            @Local(argsOnly = true) BlockPos blockPos) {
        BrewingStandBlockEntity brewingStandBlockEntity = (BrewingStandBlockEntity) Objects.requireNonNull(level.getBlockEntity(blockPos));
        VPRecipeTypes.Brewing.Input input = new VPRecipeTypes.Brewing.Input(potion, ingredient);
        RecipeHolder<? extends VPRecipeTypes.Brewing> recipeHolder = getRecipeResult(brewingStandBlockEntity, input, level);

        if (recipeHolder == null)
            return potion;

        VPBrewingStandBlockEntity.cast(brewingStandBlockEntity).setRecipeUsed(recipeHolder);

        return recipeHolder.value().assemble(input, level.registryAccess());
    }

    @Unique
    private List<RecipeHolder<?>> getRecipesToAward(ServerLevel level) {
        ArrayList<RecipeHolder<?>> list = new ArrayList<>();
        recipesUsed.reference2IntEntrySet().forEach(entry -> level.recipeAccess().byKey(entry.getKey()).ifPresent(list::add));

        return list;
    }

    @Override
    @Nullable
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {
        if (recipeHolder != null)
            recipesUsed.addTo(recipeHolder.id(), 1);
    }

    @Override
    public void awardUsedRecipes(@NonNull Player player, @NonNull List<ItemStack> itemStacks) {
        // 미사용
    }

    @Override
    public void awardUsedRecipes(@NonNull Player player) {
        List<RecipeHolder<?>> list = getRecipesToAward((ServerLevel) player.level());

        player.awardRecipes(list);
        list.stream().filter(Objects::nonNull).forEach(recipeholder -> player.triggerRecipeCrafted(recipeholder, items));

        recipesUsed.clear();
    }

    @Override
    protected void onPreRemoveSideEffects(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (level instanceof ServerLevel serverlevel)
            getRecipesToAward(serverlevel);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setQuickCheck(BlockPos pPos, BlockState pState, CallbackInfo ci) {
        quickCheck = RecipeManager.createCheck(VPRecipeTypes.BREWING.get());
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadAdditional(ValueInput valueInput, CallbackInfo ci) {
        totalBrewTime = valueInput.getIntOr("TotalBrewTime", 0);

        recipesUsed.clear();
        recipesUsed.putAll(valueInput.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveAdditional(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt("TotalBrewTime", totalBrewTime);
        valueOutput.store("RecipesUsed", RECIPES_USED_CODEC, recipesUsed);
    }

    @Redirect(method = "canPlaceItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionBrewing;isIngredient(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean redirectIsIngredient(PotionBrewing instance, ItemStack itemStack) {
        return ((ServerLevel) Objects.requireNonNull(level)).recipeAccess().propertySet(VPRecipeTypes.Brewing.INGREDIENT_SET).test(itemStack);
    }

    @Redirect(method = "canPlaceItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionBrewing;isValidInput(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean redirectIsValidInput(PotionBrewing instance, ItemStack itemStack) {
        return BrewingStandMenu.PotionSlot.mayPlaceItem(itemStack);
    }

    @Mixin(targets = "net.minecraft.world.level.block.entity.BrewingStandBlockEntity$1")
    public abstract static class ContainerDataMixin {
        @Shadow
        @Final
        BrewingStandBlockEntity this$0;

        @Inject(method = "get", at = @At("HEAD"), cancellable = true)
        public void get(int index, CallbackInfoReturnable<Integer> cir) {
            if (index == DATA_TOTAL_BREW_TIME)
                cir.setReturnValue(VPBrewingStandBlockEntity.cast(this$0).getTotalBrewTime());
        }

        @Inject(method = "set", at = @At("HEAD"), cancellable = true)
        public void set(int index, int value, CallbackInfo ci) {
            if (index != DATA_TOTAL_BREW_TIME)
                return;

            VPBrewingStandBlockEntity.cast(this$0).setTotalBrewTime(value);
            ci.cancel();
        }

        @Overwrite
        public int getCount() {
            return NUM_DATA_VALUES;
        }
    }
}
