package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.extension.world.level.block.VPLootContainerBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
public abstract class ChestBlockEntityMixin<T extends ChestBlockEntity> extends RandomizableContainerBlockEntityMixin<T> {
    @Unique
    private static final Component COMPONENT_CHEST_LOOT = Component.translatable("container.chest_loot");
    @Shadow
    @Final
    private ChestLidController chestLidController;

    @Inject(method = "lidAnimateTick", at = @At("HEAD"))
    @SuppressWarnings("unchecked")
    private static void openIfAlwaysOpen(Level level, BlockPos pos, BlockState state, ChestBlockEntity entity, CallbackInfo ci) {
        if (state.getValue(VPLootContainerBlock.ALWAYS_OPEN))
            ((ChestBlockEntityMixin<ChestBlockEntity>) (Object) entity).chestLidController.shouldBeOpen(true);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadAdditional(ValueInput input, CallbackInfo ci) {
        onLoadAdditional(input);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveAdditional(ValueOutput output, CallbackInfo ci) {
        onSaveAdditional(output);
    }

    @ModifyReturnValue(method = "getDefaultName", at = @At(value = "RETURN"))
    private Component modifyDefaultName(Component component) {
        return getBlockState().getValue(VPLootContainerBlock.LOOT) ? COMPONENT_CHEST_LOOT : component;
    }
}
