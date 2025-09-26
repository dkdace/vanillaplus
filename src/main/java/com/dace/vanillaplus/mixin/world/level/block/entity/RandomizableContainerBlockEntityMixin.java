package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.VPRegistries;
import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import com.dace.vanillaplus.custom.CustomRandomizableContainerBlockEntity;
import com.dace.vanillaplus.rebalance.modifier.LootTableModifier;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin extends BlockEntityMixin implements CustomRandomizableContainerBlockEntity {
    @Unique
    @Nullable
    @Getter
    protected LootTableModifier lootTableModifier;
    @Unique
    @Nullable
    private ResourceKey<LootTableModifier> lootTableModifierResourceKey;

    @Inject(method = "setLootTable", at = @At("TAIL"))
    private void setLootTableModifier(ResourceKey<LootTable> lootTableResourceKey, CallbackInfo ci) {
        if (lootTableResourceKey != null)
            lootTableModifierResourceKey = VPRegistries.LOOT_TABLE_MODIFIER.createResourceKey(lootTableResourceKey.location().getPath());
    }

    @Override
    public void onLoad() {
        Objects.requireNonNull(level);

        if (lootTableModifierResourceKey == null)
            return;

        Holder.Reference<LootTableModifier> reference = level.registryAccess().get(lootTableModifierResourceKey).orElse(null);
        if (reference == null)
            return;

        lootTableModifier = reference.value();
        if (getBlockState().hasProperty(CustomLootContainerBlock.LOOT))
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CustomLootContainerBlock.LOOT, true));
    }
}
