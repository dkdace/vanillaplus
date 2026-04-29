package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.data.ReloadableDataManager;
import com.dace.vanillaplus.extension.world.level.block.VPLootContainerBlock;
import com.dace.vanillaplus.extension.world.level.block.entity.VPRandomizableContainerBlockEntity;
import com.dace.vanillaplus.world.LootTableReward;
import lombok.NonNull;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    @Nullable
    protected ResourceKey<LootTable> originalLootTable;

    @Unique
    private void init(@Nullable ResourceKey<LootTable> lootTableResourceKey) {
        if (lootTableResourceKey == null || !(level instanceof ServerLevel serverLevel) || !getBlockState().hasProperty(VPLootContainerBlock.LOOT))
            return;

        this.originalLootTable = lootTableResourceKey;
        if (getLootTableReward() != null)
            serverLevel.setBlockAndUpdate(worldPosition, getBlockState().setValue(VPLootContainerBlock.LOOT, true));

        setChanged();
    }

    @Unique
    protected void onLoadAdditional(@NonNull ValueInput valueInput) {
        originalLootTable = valueInput.read("OriginalLootTable", LootTable.KEY_CODEC).orElse(null);
    }

    @Unique
    protected void onSaveAdditional(@NonNull ValueOutput valueOutput) {
        valueOutput.storeNullable("OriginalLootTable", LootTable.KEY_CODEC, originalLootTable);
    }

    @Override
    public void onLoad() {
        init(lootTable);
    }

    @Override
    @Nullable
    public LootTableReward getLootTableReward() {
        return originalLootTable == null ? null : ReloadableDataManager.LOOT_TABLE_REWARD.get(originalLootTable).orElse(null);
    }

    @Inject(method = "setLootTable", at = @At("TAIL"))
    private void initOnSetLootTable(ResourceKey<LootTable> lootTable, CallbackInfo ci) {
        init(lootTable);
    }
}
