package com.dace.vanillaplus.mixin.core;

import com.dace.vanillaplus.block.LayeredCauldronBlockEntity;
import com.dace.vanillaplus.extension.VPMixin;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(CauldronInteraction.class)
public interface CauldronInteractionMixin extends VPMixin<CauldronInteraction> {
    @ModifyExpressionValue(method = "lambda$bootStrap$1", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;is(Lnet/minecraft/core/Holder;)Z"))
    private static boolean modifyPotionInteractionCondition0(boolean original) {
        return true;
    }

    @ModifyExpressionValue(method = "lambda$bootStrap$5", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;is(Lnet/minecraft/core/Holder;)Z"))
    private static boolean modifyPotionInteractionCondition1(boolean original) {
        return true;
    }

    @Inject(method = "lambda$bootStrap$1", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void addPotionOnInteract0(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand,
                                             ItemStack itemStack, CallbackInfoReturnable<InteractionResult> cir, @Local PotionContents potionContents) {
        if (level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
            layeredCauldronBlockEntity.addPotionContents(potionContents);
    }

    @Inject(method = "lambda$bootStrap$5", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void addPotionOnInteract1(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand,
                                             ItemStack itemStack, CallbackInfoReturnable<InteractionResult> cir, @Local PotionContents potionContents) {
        if (level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
            layeredCauldronBlockEntity.addPotionContents(potionContents);
    }

    @Redirect(method = "lambda$bootStrap$4", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack redirectPotionOnInteract(Item item, Holder<Potion> potionHolder, @Local(argsOnly = true) Level level,
                                                      @Local(argsOnly = true) BlockPos blockPos) {
        ItemStack itemStack = new ItemStack(item);

        if (level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
            itemStack.set(DataComponents.POTION_CONTENTS, layeredCauldronBlockEntity.getPotionContents());

        return itemStack;
    }

    @ModifyArg(method = "fillWaterInteraction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/cauldron/CauldronInteraction;emptyBucket(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/sounds/SoundEvent;)Lnet/minecraft/world/InteractionResult;"),
            index = 5)
    private static BlockState modifyWaterBucketInteractionBlockState(BlockState blockState, @Local(argsOnly = true) Level level,
                                                                     @Local(argsOnly = true) BlockPos blockPos) {
        if (level.isClientSide())
            return blockState;

        if (!level.getBlockState(blockPos).is(Blocks.WATER_CAULDRON))
            level.setBlockAndUpdate(blockPos, blockState);

        if (!(level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity))
            return blockState;

        for (int i = 0; i < LayeredCauldronBlock.MAX_FILL_LEVEL; i++)
            layeredCauldronBlockEntity.addPotionContents(null);

        return blockState.setValue(LayeredCauldronBlock.LEVEL, 3);
    }

    @ModifyArg(method = "lambda$bootStrap$3", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/cauldron/CauldronInteraction;fillBucket(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Predicate;Lnet/minecraft/sounds/SoundEvent;)Lnet/minecraft/world/InteractionResult;"),
            index = 7)
    private static Predicate<BlockState> modifyWaterBucketFillCondition(Predicate<BlockState> predicate, @Local(argsOnly = true) Level level,
                                                                        @Local(argsOnly = true) BlockPos blockPos) {
        return predicate.and(blockState -> !(level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity)
                || layeredCauldronBlockEntity.hasPureWater());
    }

    @Definition(id = "pLevel", local = @Local(type = Level.class, argsOnly = true))
    @Definition(id = "isClientSide", method = "Lnet/minecraft/world/level/Level;isClientSide()Z")
    @Expression("pLevel.isClientSide() == false")
    @ModifyExpressionValue(method = "dyedItemIteration", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean modifyDyedItemWashCondition(boolean condition, @Local(argsOnly = true) Level level,
                                                       @Local(argsOnly = true) BlockPos blockPos) {
        return level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity
                ? condition && layeredCauldronBlockEntity.hasPureWater()
                : condition;
    }

    @Definition(id = "pLevel", local = @Local(type = Level.class, argsOnly = true))
    @Definition(id = "isClientSide", method = "Lnet/minecraft/world/level/Level;isClientSide()Z")
    @Expression("pLevel.isClientSide() == false")
    @ModifyExpressionValue(method = "bannerInteraction", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean modifyBannerWashCondition(boolean condition, @Local(argsOnly = true) Level level,
                                                     @Local(argsOnly = true) BlockPos blockPos) {
        return level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity
                ? condition && layeredCauldronBlockEntity.hasPureWater()
                : condition;
    }

    @Definition(id = "pLevel", local = @Local(type = Level.class, argsOnly = true))
    @Definition(id = "isClientSide", method = "Lnet/minecraft/world/level/Level;isClientSide()Z")
    @Expression("pLevel.isClientSide() == false")
    @ModifyExpressionValue(method = "shulkerBoxInteraction", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean modifyShulkerBoxWashCondition(boolean condition, @Local(argsOnly = true) Level level,
                                                         @Local(argsOnly = true) BlockPos blockPos) {
        return level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity
                ? condition && layeredCauldronBlockEntity.hasPureWater()
                : condition;
    }

    @Inject(method = "bootStrap", at = @At(value = "FIELD",
            target = "Lnet/minecraft/core/cauldron/CauldronInteraction;LAVA:Lnet/minecraft/core/cauldron/CauldronInteraction$InteractionMap;",
            opcode = Opcodes.GETSTATIC))
    private static void addArrowInteraction(CallbackInfo ci, @Local(ordinal = 1) Map<Item, CauldronInteraction> map1) {
        map1.put(Items.ARROW, (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if (!level.isClientSide() && level.getBlockEntity(blockPos) instanceof LayeredCauldronBlockEntity layeredCauldronBlockEntity) {
                int count = Math.min(itemStack.getCount(), 8);
                itemStack.consume(count, player);

                ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW, count);
                tippedArrow.set(DataComponents.POTION_CONTENTS, layeredCauldronBlockEntity.getPotionContents());

                if (itemStack.isEmpty())
                    player.setItemInHand(interactionHand, tippedArrow);
                else {
                    if (!player.getInventory().add(tippedArrow))
                        player.drop(tippedArrow, false);

                    player.setItemInHand(interactionHand, itemStack);
                }

                player.awardStat(Stats.USE_CAULDRON);

                LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
            }

            return InteractionResult.SUCCESS;
        });
    }
}
