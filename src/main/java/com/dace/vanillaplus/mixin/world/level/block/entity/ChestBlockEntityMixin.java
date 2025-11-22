package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.extension.world.level.block.VPLootContainerBlock;
import com.dace.vanillaplus.extension.world.level.block.entity.VPChestBlockEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin<T extends ChestBlockEntity> extends RandomizableContainerBlockEntityMixin<T> implements VPChestBlockEntity<T> {
    @Unique
    private static final String COMPONENT_CHEST_LOOT = "container.chestLoot";
    @Shadow
    @Final
    private ChestLidController chestLidController;

    @Inject(method = "lidAnimateTick", at = @At("HEAD"))
    private static void openIfAlwaysOpen(Level level, BlockPos blockPos, BlockState blockState, ChestBlockEntity chestBlockEntity, CallbackInfo ci) {
        if (blockState.getValue(VPLootContainerBlock.ALWAYS_OPEN))
            VPChestBlockEntity.cast(chestBlockEntity).openLid();
    }

    @Override
    public void openLid() {
        chestLidController.shouldBeOpen(true);
    }

    @ModifyReturnValue(method = "getDefaultName", at = @At(value = "RETURN"))
    private Component modifyDefaultName(Component component) {
        return getBlockState().getValue(VPLootContainerBlock.LOOT) ? Component.translatable(COMPONENT_CHEST_LOOT) : component;
    }
}
