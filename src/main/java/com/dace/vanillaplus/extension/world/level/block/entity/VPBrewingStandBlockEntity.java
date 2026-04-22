package com.dace.vanillaplus.extension.world.level.block.entity;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.item.crafting.BrewingRecipe;
import lombok.NonNull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

/**
 * {@link BrewingStandBlockEntity}를 확장하는 인터페이스.
 */
public interface VPBrewingStandBlockEntity extends VPMixin<BrewingStandBlockEntity>, RecipeCraftingHolder {
    /** 전체 양조 시간 데이터 인덱스 */
    int DATA_TOTAL_BREW_TIME = 2;
    /** 데이터 수 */
    int NUM_DATA_VALUES = 3;

    @NonNull
    static VPBrewingStandBlockEntity cast(@NonNull BrewingStandBlockEntity object) {
        return (VPBrewingStandBlockEntity) object;
    }

    /**
     * @return 전체 양조 시간
     */
    int getTotalBrewTime();

    /**
     * @param brewTime 전체 양조 시간
     */
    void setTotalBrewTime(int brewTime);

    /**
     * 플레이어에게 사용한 제작법을 지급한다.
     *
     * @param player 대상 플레이어
     */
    void awardUsedRecipes(@NonNull Player player);

    /**
     * @return 제작법 캐시
     */
    @NonNull
    RecipeManager.CachedCheck<BrewingRecipe.Input, ? extends BrewingRecipe> getQuickCheck();
}
