package com.dace.vanillaplus.mixin.world.entity.animal.chicken;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends MobMixin<Chicken> {
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/TemptGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;DLjava/util/function/Predicate;Z)V"),
            index = 2)
    private Predicate<ItemStack> modifyTemptFoodCondition(Predicate<ItemStack> items) {
        return itemStack -> isBaby() ? itemStack.is(VPTags.Items.BABY_CHICKEN_FOOD) : items.test(itemStack);
    }

    @ModifyExpressionValue(method = "isFood", at = @At(value = "FIELD",
            target = "Lnet/minecraft/tags/ItemTags;CHICKEN_FOOD:Lnet/minecraft/tags/TagKey;", opcode = Opcodes.GETSTATIC))
    private TagKey<Item> modifyFood(TagKey<Item> itemTagKey) {
        return isBaby() ? VPTags.Items.BABY_CHICKEN_FOOD : itemTagKey;
    }
}
