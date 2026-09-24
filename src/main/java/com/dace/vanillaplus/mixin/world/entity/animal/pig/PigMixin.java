package com.dace.vanillaplus.mixin.world.entity.animal.pig;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(Pig.class)
public abstract class PigMixin extends MobMixin<Pig> {
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/TemptGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;DLjava/util/function/Predicate;Z)V",
            ordinal = 1), index = 2)
    private Predicate<ItemStack> modifyTemptFoodCondition(Predicate<ItemStack> items) {
        return itemStack -> isBaby() ? itemStack.is(VPTags.Items.BABY_PIG_FOOD) : items.test(itemStack);
    }

    @ModifyExpressionValue(method = "isFood", at = @At(value = "FIELD", target = "Lnet/minecraft/tags/ItemTags;PIG_FOOD:Lnet/minecraft/tags/TagKey;",
            opcode = Opcodes.GETSTATIC))
    private TagKey<Item> modifyFood(TagKey<Item> itemTagKey) {
        return isBaby() ? VPTags.Items.BABY_PIG_FOOD : itemTagKey;
    }
}
