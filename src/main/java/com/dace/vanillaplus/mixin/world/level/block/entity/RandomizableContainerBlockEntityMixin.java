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
    @Unique
    @Nullable
    protected ResourceKey<LootTable> lootTableResourceKey;
    @Shadow
    @Nullable
    protected ResourceKey<LootTable> lootTable;

    @Unique
    private void init(@NonNull ResourceKey<LootTable> lootTableResourceKey) {
        if (!(level instanceof ServerLevel serverLevel) || !getBlockState().hasProperty(VPLootContainerBlock.LOOT))
            return;

        this.lootTableResourceKey = lootTableResourceKey;
        if (getLootTableReward() != null)
            serverLevel.setBlockAndUpdate(worldPosition, getBlockState().setValue(VPLootContainerBlock.LOOT, true));

        setChanged();
    }

    @Unique
    protected void onLoadAdditional(@NonNull ValueInput valueInput) {
        lootTableResourceKey = valueInput.read("OriginalLootTable", LootTable.KEY_CODEC).orElse(null);
    }

    @Unique
    protected void onSaveAdditional(@NonNull ValueOutput valueOutput) {
        valueOutput.storeNullable("OriginalLootTable", LootTable.KEY_CODEC, lootTableResourceKey);
    }

    @Override
    public void onLoad() {
        if (lootTable != null)
            init(lootTable);
    }

    @Override
    @Nullable
    public LootTableReward getLootTableReward() {
        return lootTableResourceKey == null ? null : ReloadableDataManager.LOOT_TABLE_REWARD.get(lootTableResourceKey).orElse(null);
    }

    @Inject(method = "setLootTable", at = @At("TAIL"))
    private void initOnSetLootTable(ResourceKey<LootTable> lootTable, CallbackInfo ci) {
        if (lootTable != null)
            init(lootTable);
    }
}
