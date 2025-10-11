package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.data.LootTableReward;
import com.dace.vanillaplus.extension.VPLootContainerBlock;
import com.dace.vanillaplus.extension.VPRandomizableContainerBlockEntity;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin extends BlockEntityMixin implements VPRandomizableContainerBlockEntity {
    @Unique
    @Nullable
    @Getter
    protected LootTableReward lootTableReward;
    @Unique
    @Nullable
    private ResourceKey<LootTableReward> lootTableRewardResourceKey;

    @Inject(method = "setLootTable", at = @At("TAIL"))
    private void setLootTableReward(ResourceKey<LootTable> lootTableResourceKey, CallbackInfo ci) {
        if (lootTableResourceKey == null)
            return;

        lootTableRewardResourceKey = VPRegistries.LOOT_TABLE_REWARD.createResourceKey(lootTableResourceKey.location().getPath());

        if (lootTableReward == null)
            onLoad();
    }

    @Override
    public void onLoad() {
        if (level == null || lootTableRewardResourceKey == null)
            return;

        lootTableReward = VPRegistries.getValue(lootTableRewardResourceKey);

        if (lootTableReward != null && getBlockState().hasProperty(VPLootContainerBlock.LOOT))
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(VPLootContainerBlock.LOOT, true));
    }
}
