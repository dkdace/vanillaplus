package com.dace.vanillaplus.mixin.core.cauldron;

import com.dace.vanillaplus.data.registryobject.VPSoundEvents;
import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.world.block.BlockModifier;
import com.dace.vanillaplus.world.block.entity.WaterCauldronBlockEntity;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
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
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(CauldronInteractions.class)
public abstract class CauldronInteractionsMixin implements VPMixin<CauldronInteractions> {
    @Shadow
    @Final
    public static CauldronInteraction.Dispatcher WATER;

    @Shadow
    static InteractionResult emptyBucket(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack itemInHand, BlockState newState,
                                         SoundEvent soundEvent) {
        throw new UnsupportedOperationException();
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
            if (level.isClientSide() || !(level.getBlockEntity(blockPos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity))
                return;

            int count = Math.min(itemStack.getCount(), waterCauldronModifier.getMaxTippedArrowCount());
            itemStack.consume(count, player);

            ItemStack newItemStack = new ItemStack(Items.TIPPED_ARROW, count);
            newItemStack.set(DataComponents.POTION_CONTENTS, waterCauldronBlockEntity.getPotionContents());

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
                && level.getBlockEntity(blockPos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity) {
            DyeColor dyeColor = itemStack.get(DataComponents.DYE);

            if (dyeColor != null) {
                itemStack.consume(1, player);

                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                level.playSound(null, blockPos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1, 1);

                waterCauldronBlockEntity.addDyeColor(dyeColor);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Unique
    private static boolean isNotWashable(boolean condition, @NonNull Level level, @NonNull BlockPos blockPos) {
        if (getDataModifier().isPresent())
            return condition || level.getBlockEntity(blockPos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity
                    && !waterCauldronBlockEntity.hasPureWater();

        return condition;
    }

    @ModifyExpressionValue(method = {"lambda$bootStrap$0", "lambda$bootStrap$4"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;is(Lnet/minecraft/core/Holder;)Z"))
    private static boolean modifyPotionFillCondition(boolean isWater) {
        return getDataModifier().isPresent() || isWater;
    }

    @Inject(method = {"lambda$bootStrap$0", "lambda$bootStrap$4"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void addPotionContents(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack itemInHand,
                                          CallbackInfoReturnable<InteractionResult> cir, @Local(name = "potion") PotionContents potion) {
        if (getDataModifier().isPresent() && level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
            waterCauldronBlockEntity.addPotionContents(potion);
    }

    @Redirect(method = "lambda$bootStrap$3", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack redirectPotionOnTake(Item item, Holder<Potion> potion, @Local(argsOnly = true) Level level,
                                                  @Local(argsOnly = true) BlockPos pos) {
        if (getDataModifier().isEmpty())
            return PotionContents.createItemStack(item, potion);

        ItemStack itemStack = new ItemStack(item);

        if (level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
            itemStack.set(DataComponents.POTION_CONTENTS, waterCauldronBlockEntity.getPotionContents());

        return itemStack;
    }

    @Redirect(method = "fillWaterInteraction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/cauldron/CauldronInteractions;emptyBucket(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/sounds/SoundEvent;)Lnet/minecraft/world/InteractionResult;"))
    private static InteractionResult addWater(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack itemInHand,
                                              BlockState newState, SoundEvent soundEvent) {
        if (getDataModifier().isEmpty())
            return emptyBucket(level, pos, player, hand, itemInHand, newState, soundEvent);

        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        int count = 1;
        BlockState currentBlockState = level.getBlockState(pos);

        if (currentBlockState.is(Blocks.WATER_CAULDRON))
            count += currentBlockState.getValue(LayeredCauldronBlock.LEVEL);
        else
            currentBlockState = Blocks.WATER_CAULDRON.defaultBlockState();

        for (int i = count; i <= LayeredCauldronBlock.MAX_FILL_LEVEL; i++) {
            level.setBlockAndUpdate(pos, currentBlockState.setValue(LayeredCauldronBlock.LEVEL, i));
            if (level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
                waterCauldronBlockEntity.addPotionContents(null);
        }

        return emptyBucket(level, pos, player, hand, itemInHand, level.getBlockState(pos), soundEvent);
    }

    @ModifyArg(method = "lambda$bootStrap$1", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/cauldron/CauldronInteractions;fillBucket(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Predicate;Lnet/minecraft/sounds/SoundEvent;)Lnet/minecraft/world/InteractionResult;"),
            index = 7)
    private static Predicate<BlockState> modifyWaterTakeCondition(Predicate<BlockState> canFill, @Local(argsOnly = true) Level level,
                                                                  @Local(argsOnly = true) BlockPos pos) {
        if (getDataModifier().isPresent())
            return canFill.and(ignored -> !(level.getBlockEntity(pos) instanceof WaterCauldronBlockEntity waterCauldronBlockEntity)
                    || waterCauldronBlockEntity.hasPureWater());

        return canFill;
    }

    @Definition(id = "itemInHand", local = @Local(type = ItemStack.class, argsOnly = true))
    @Definition(id = "has", method = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z")
    @Definition(id = "DYED_COLOR", field = "Lnet/minecraft/core/component/DataComponents;DYED_COLOR:Lnet/minecraft/core/component/DataComponentType;")
    @Expression("itemInHand.has(DYED_COLOR) == false")
    @ModifyExpressionValue(method = "dyedItemIteration", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean modifyDyedItemWashCondition(boolean condition, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return isNotWashable(condition, level, pos);
    }

    @ModifyExpressionValue(method = "bannerInteraction", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private static boolean modifyBannerWashCondition(boolean isEmpty, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return isNotWashable(isEmpty, level, pos);
    }

    @Definition(id = "block", local = @Local(type = Block.class, name = "block"))
    @Definition(id = "ShulkerBoxBlock", type = ShulkerBoxBlock.class)
    @Expression("block instanceof ShulkerBoxBlock == false")
    @ModifyExpressionValue(method = "shulkerBoxInteraction", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean modifyShulkerBoxWashCondition(boolean condition, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return isNotWashable(condition, level, pos);
    }

    @Inject(method = "bootStrap", at = @At(value = "FIELD",
            target = "Lnet/minecraft/core/cauldron/CauldronInteractions;LAVA:Lnet/minecraft/core/cauldron/CauldronInteraction$Dispatcher;",
            ordinal = 0, opcode = Opcodes.GETSTATIC))
    private static void addItemInteractions(CallbackInfo ci) {
        WATER.put(Items.ARROW, CauldronInteractionsMixin::getArrowInteractionResult);
        WATER.put(ItemTags.DYES, CauldronInteractionsMixin::getDyeInteractionResult);
    }
}
