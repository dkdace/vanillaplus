package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.data.LootTableReward;
import com.dace.vanillaplus.extension.VPLootContainerBlock;
import com.dace.vanillaplus.extension.VPRandomizableContainerBlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin<T extends RandomizableContainerBlockEntity> extends BlockEntityMixin<T> implements VPRandomizableContainerBlockEntity<T> {
    @Shadow
    @Nullable
    protected ResourceKey<LootTable> lootTable;
    @Unique
    private boolean hasReward = false;

    @Inject(method = "setLootTable", at = @At("TAIL"))
    private void setLootTableReward(ResourceKey<LootTable> lootTableResourceKey, CallbackInfo ci) {
        if (lootTableResourceKey != null && !hasReward)
            onLoad();
    }

    @Override
    public void onLoad() {
        if (hasReward || level == null || getLootTableReward() == null || !getBlockState().hasProperty(VPLootContainerBlock.LOOT))
            return;

        hasReward = true;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(VPLootContainerBlock.LOOT, true));
    }

    @Override
    @Nullable
    public LootTableReward getLootTableReward() {
        return lootTable == null ? null : LootTableReward.fromLootTable(lootTable);
    }
}
