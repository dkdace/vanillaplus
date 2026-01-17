package com.dace.vanillaplus.mixin.core.cauldron;

import com.dace.vanillaplus.block.LayeredCauldronBlockEntity;
import com.dace.vanillaplus.data.modifier.BlockModifier;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.registryobject.VPSoundEvents;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.UnknownNullability;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(CauldronInteraction.class)
public interface CauldronInteractionMixin extends VPMixin<CauldronInteraction> {
    @Shadow
    @UnknownNullability
    static InteractionResult emptyBucket(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack filledItemStack,
                                         BlockState blockState, SoundEvent emptySound) {
        return null;
    }

    @Unique
    @NonNull
    private static Optional<BlockModifier.WaterCauldronModifier> getDataModifier() {
        return VPModifiableData.getDataModifier(Blocks.WATER_CAULDRON, BlockModifier.WaterCauldronModifier.class);
    }

    @Unique
    @NonNull
    private static InteractionResult getArrowInteractionResult(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos,
                                                               @NonNull Player player, @NonNull InteractionHand interactionHand,
                                                               @NonNull ItemStack itemStack) {
        getDataModifier().ifPresent(waterCauldronModifier -> {
            if (level.isClientSide() || !(level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity))
                return;

            int count = Math.min(itemStack.getCount(), waterCauldronModifier.getMaxTippedArrowCount());
            itemStack.consume(count, player);

            ItemStack newItemStack = new ItemStack(Items.TIPPED_ARROW, count);
            newItemStack.set(DataComponents.POTION_CONTENTS, layeredCauldronBlockEntity.getPotionContents());

            if (itemStack.isEmpty())
                player.setItemInHand(interactionHand, newItemStack);
            else {
                if (!player.getInventory().add(newItemStack))
                    player.drop(newItemStack, false);

                player.setItemInHand(interactionHand, itemStack);
            }

            player.awardStat(Stats.USE_CAULDRON);
            level.playSound(null, blockPos, VPSoundEvents.ARROW_TIPPED.get(), SoundSource.BLOCKS, 1, 1);

            LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
        });

        return InteractionResult.SUCCESS;
    }

    @Unique
    @NonNull
    private static InteractionResult getDyeInteractionResult(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos,
                                                             @NonNull Player player, @NonNull InteractionHand interactionHand,
                                                             @NonNull ItemStack itemStack) {
        if (getDataModifier().isPresent() && !level.isClientSide()
                && level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity) {
            DyeItem item = (DyeItem) itemStack.getItem();
            itemStack.consume(1, player);

            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            level.playSound(null, blockPos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1, 1);

            layeredCauldronBlockEntity.addDyeColor(item.getDyeColor());
        }

        return InteractionResult.SUCCESS;
    }

    @Unique
    private static boolean isNotWashable(boolean condition, @NonNull Level level, @NonNull BlockPos blockPos) {
        if (getDataModifier().isPresent())
            return condition || level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity
                    && !layeredCauldronBlockEntity.hasPureWater();

        return condition;
    }

    @ModifyExpressionValue(method = "lambda$bootStrap$1", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;is(Lnet/minecraft/core/Holder;)Z"))
    private static boolean modifyPotionTakeCondition(boolean original) {
        return getDataModifier().isPresent() || original;
    }

    @ModifyExpressionValue(method = "lambda$bootStrap$5", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;is(Lnet/minecraft/core/Holder;)Z"))
    private static boolean modifyPotionFillCondition(boolean original) {
        return getDataModifier().isPresent() || original;
    }

    @Inject(method = "lambda$bootStrap$1", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void addPotionOnFill0(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand,
                                         ItemStack itemStack, CallbackInfoReturnable<InteractionResult> cir, @Local PotionContents potionContents) {
        if (getDataModifier().isPresent() && level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
            layeredCauldronBlockEntity.addPotionContents(potionContents);
    }

    @Inject(method = "lambda$bootStrap$5", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void addPotionOnFill1(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand,
                                         ItemStack itemStack, CallbackInfoReturnable<InteractionResult> cir, @Local PotionContents potionContents) {
        if (getDataModifier().isEmpty())
            return;

        if (potionContents != null && level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
            layeredCauldronBlockEntity.addPotionContents(potionContents);
    }

    @Redirect(method = "lambda$bootStrap$4", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack redirectPotionOnTake(Item item, Holder<Potion> potionHolder, @Local(argsOnly = true) BlockState blockState,
                                                  @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos blockPos) {
        if (getDataModifier().isEmpty())
            return PotionContents.createItemStack(item, potionHolder);

        ItemStack itemStack = new ItemStack(item);

        if (level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
            itemStack.set(DataComponents.POTION_CONTENTS, layeredCauldronBlockEntity.getPotionContents());

        return itemStack;
    }

    @Redirect(method = "fillWaterInteraction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/cauldron/CauldronInteraction;emptyBucket(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/sounds/SoundEvent;)Lnet/minecraft/world/InteractionResult;"))
    private static InteractionResult addPotionOnFillWater(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand,
                                                          ItemStack filledItemStack, BlockState blockState, SoundEvent emptySound) {
        if (getDataModifier().isEmpty())
            return emptyBucket(level, blockPos, player, interactionHand, filledItemStack, blockState, emptySound);

        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        int count = 1;
        BlockState currentBlockState = level.getBlockState(blockPos);

        if (currentBlockState.is(Blocks.WATER_CAULDRON))
            count += currentBlockState.getValue(LayeredCauldronBlock.LEVEL);
        else
            currentBlockState = Blocks.WATER_CAULDRON.defaultBlockState();

        for (int i = count; i <= LayeredCauldronBlock.MAX_FILL_LEVEL; i++) {
            level.setBlockAndUpdate(blockPos, currentBlockState.setValue(LayeredCauldronBlock.LEVEL, i));
            if (level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
                layeredCauldronBlockEntity.addPotionContents(null);
        }

        return emptyBucket(level, blockPos, player, interactionHand, filledItemStack, level.getBlockState(blockPos), emptySound);
    }

    @ModifyArg(method = "lambda$bootStrap$3", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/cauldron/CauldronInteraction;fillBucket(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Predicate;Lnet/minecraft/sounds/SoundEvent;)Lnet/minecraft/world/InteractionResult;"),
            index = 7)
    private static Predicate<BlockState> modifyWaterTakeCondition(Predicate<BlockState> predicate, @Local(argsOnly = true) BlockState blockState,
                                                                  @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos blockPos) {
        if (getDataModifier().isPresent())
            return predicate.and(ignored -> !(level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
                    || layeredCauldronBlockEntity.hasPureWater());

        return predicate;
    }

    @Definition(id = "pStack", local = @Local(type = ItemStack.class, argsOnly = true))
    @Definition(id = "has", method = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z")
    @Definition(id = "DYED_COLOR", field = "Lnet/minecraft/core/component/DataComponents;DYED_COLOR:Lnet/minecraft/core/component/DataComponentType;")
    @Expression("pStack.has(DYED_COLOR) == false")
    @ModifyExpressionValue(method = "dyedItemIteration", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean modifyDyedItemWashCondition(boolean condition, @Local(argsOnly = true) Level level,
                                                       @Local(argsOnly = true) BlockPos blockPos) {
        return isNotWashable(condition, level, blockPos);
    }

    @ModifyExpressionValue(method = "bannerInteraction", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private static boolean modifyBannerWashCondition(boolean condition, @Local(argsOnly = true) Level level,
                                                     @Local(argsOnly = true) BlockPos blockPos) {
        return isNotWashable(condition, level, blockPos);
    }

    @Definition(id = "block", local = @Local(type = Block.class))
    @Definition(id = "ShulkerBoxBlock", type = ShulkerBoxBlock.class)
    @Expression("block instanceof ShulkerBoxBlock == false")
    @ModifyExpressionValue(method = "shulkerBoxInteraction", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean modifyShulkerBoxWashCondition(boolean condition, @Local(argsOnly = true) Level level,
                                                         @Local(argsOnly = true) BlockPos blockPos) {
        return isNotWashable(condition, level, blockPos);
    }

    @Inject(method = "bootStrap", at = @At(value = "FIELD",
            target = "Lnet/minecraft/core/cauldron/CauldronInteraction;LAVA:Lnet/minecraft/core/cauldron/CauldronInteraction$InteractionMap;",
            opcode = Opcodes.GETSTATIC))
    private static void addItemInteractions(CallbackInfo ci, @Local(ordinal = 1) Map<Item, CauldronInteraction> waterMap) {
        waterMap.put(Items.ARROW, CauldronInteractionMixin::getArrowInteractionResult);
        waterMap.put(Items.WHITE_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.LIGHT_GRAY_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.GRAY_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.BLACK_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.BROWN_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.RED_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.ORANGE_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.YELLOW_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.LIME_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.GREEN_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.CYAN_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.LIGHT_BLUE_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.BLUE_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.PURPLE_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.MAGENTA_DYE, CauldronInteractionMixin::getDyeInteractionResult);
        waterMap.put(Items.PINK_DYE, CauldronInteractionMixin::getDyeInteractionResult);
    }
}
