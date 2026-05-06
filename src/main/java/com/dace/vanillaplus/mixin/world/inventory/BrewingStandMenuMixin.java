package com.dace.vanillaplus.mixin.world.inventory;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.inventory.VPBrewingStandMenu;
import com.dace.vanillaplus.extension.world.level.block.entity.VPBrewingStandBlockEntity;
import com.dace.vanillaplus.world.block.modifier.BrewingStandBlockModifier;
import com.dace.vanillaplus.world.item.crafting.BrewingRecipe;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandMenu.class)
public abstract class BrewingStandMenuMixin implements VPBrewingStandMenu {
    @Unique
    private static final int INGREDIENT_SLOT_X = 79;
    @Unique
    private static final int INGREDIENT_SLOT_Y = 17;
    @Shadow
    @Final
    private static int INGREDIENT_SLOT;

    @Unique
    private RecipePropertySet ingredientItemTest;
    @Shadow
    @Final
    private ContainerData brewingStandData;

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/SimpleContainerData;<init>(I)V"))
    private static int modifyContainerDataCount0(int count) {
        return VPBrewingStandBlockEntity.NUM_DATA_VALUES;
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/BrewingStandMenu;checkContainerDataCount(Lnet/minecraft/world/inventory/ContainerData;I)V"),
            index = 1)
    private static int modifyContainerDataCount1(int expected) {
        return VPBrewingStandBlockEntity.NUM_DATA_VALUES;
    }

    @Override
    public int getTotalBrewTime() {
        return brewingStandData.get(VPBrewingStandBlockEntity.DATA_TOTAL_BREW_TIME);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",
            at = @At("TAIL"))
    private void setIngredientItemTest(int containerId, Inventory inventory, Container brewingStand, ContainerData brewingStandData, CallbackInfo ci) {
        ingredientItemTest = inventory.player.level().recipeAccess().propertySet(BrewingRecipe.INGREDIENT_SET);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/BrewingStandMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;",
                    ordinal = 3))
    private Slot modifyIngredientSlot(Slot slot, @Local(argsOnly = true) Container brewingStand) {
        return BrewingStandBlockModifier.get().isUseDataDrivenRecipe() ?
                new Slot(brewingStand, INGREDIENT_SLOT, INGREDIENT_SLOT_X, INGREDIENT_SLOT_Y) {
                    @Override
                    public boolean mayPlace(@NonNull ItemStack itemStack) {
                        return ingredientItemTest.test(itemStack);
                    }
                } : slot;
    }

    @Mixin(BrewingStandMenu.PotionSlot.class)
    public abstract static class PotionSlotMixin implements VPMixin<BrewingStandMenu.PotionSlot> {
        @ModifyReturnValue(method = "mayPlaceItem", at = @At("RETURN"))
        private static boolean modifyPlaceableItem(boolean original, @Local(argsOnly = true) ItemStack itemStack) {
            return BrewingStandBlockModifier.get().isUseDataDrivenRecipe() ? itemStack.is(BrewingRecipe.getPotionContainers()) : original;
        }

        @Definition(id = "potionBrewing", field = "Lnet/minecraft/world/inventory/BrewingStandMenu$PotionSlot;potionBrewing:Lnet/minecraft/world/item/alchemy/PotionBrewing;")
        @Expression("this.potionBrewing != null")
        @ModifyExpressionValue(method = "mayPlace", at = @At("MIXINEXTRAS:EXPRESSION"))
        public boolean modifyPlaceCondition(boolean condition) {
            return !BrewingStandBlockModifier.get().isUseDataDrivenRecipe() && condition;
        }

        @Inject(method = "onTake", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/advancements/criterion/BrewedPotionTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/Holder;)V"))
        private void awardUsedRecipes(Player player, ItemStack carried, CallbackInfo ci) {
            if (getThis().container instanceof VPBrewingStandBlockEntity vpBrewingStandBlockEntity)
                vpBrewingStandBlockEntity.awardUsedRecipes(player);
        }
    }
}
