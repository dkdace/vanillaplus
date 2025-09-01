package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.custom.CustomChestBlockEntity;
import com.dace.vanillaplus.custom.CustomLootContainerBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends BlockEntityMixin implements CustomChestBlockEntity {
    @Unique
    @Getter
    @Setter
    private int xp = 25;

    @Shadow
    @Final
    private ChestLidController chestLidController;

    @Inject(method = "lidAnimateTick", at = @At("HEAD"))
    private static void openIfAlwaysOpen(Level level, BlockPos blockPos, BlockState blockState, ChestBlockEntity chestBlockEntity, CallbackInfo ci) {
        if (blockState.getValue(CustomLootContainerBlock.ALWAYS_OPEN))
            ((CustomChestBlockEntity) chestBlockEntity).openLid();
    }

    @ModifyReturnValue(method = "getDefaultName", at = @At(value = "RETURN"))
    private Component modifyDefaultName(Component component) {
        return getBlockState().getValue(CustomLootContainerBlock.LOOT) ? Component.translatable("container.chestLoot") : component;
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    protected void addLoadData(ValueInput valueInput, CallbackInfo ci) {
        xp = valueInput.getIntOr("xp", xp);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    protected void addSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt("xp", xp);
    }

    @Override
    public void openLid() {
        chestLidController.shouldBeOpen(true);
    }
}
