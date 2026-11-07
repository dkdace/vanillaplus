package com.dace.vanillaplus.mixin.world.entity.animal.rabbit;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(Rabbit.class)
public abstract class RabbitMixin extends MobMixin<Rabbit> {
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/TemptGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;DLjava/util/function/Predicate;Z)V"),
            index = 2)
    private Predicate<ItemStack> modifyTemptFoodCondition(Predicate<ItemStack> items) {
        return itemStack -> isBaby() ? itemStack.is(VPTags.Items.BABY_RABBIT_FOOD) : items.test(itemStack);
    }

    @ModifyExpressionValue(method = "isFood", at = @At(value = "FIELD",
            target = "Lnet/minecraft/tags/ItemTags;RABBIT_FOOD:Lnet/minecraft/tags/TagKey;", opcode = Opcodes.GETSTATIC))
    private TagKey<Item> modifyFood(TagKey<Item> itemTagKey) {
        return isBaby() ? VPTags.Items.BABY_RABBIT_FOOD : itemTagKey;
    }
}
