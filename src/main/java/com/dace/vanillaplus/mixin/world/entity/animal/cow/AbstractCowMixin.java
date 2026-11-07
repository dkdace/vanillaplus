package com.dace.vanillaplus.mixin.world.entity.animal.cow;

import com.dace.vanillaplus.data.VPTags;
import com.dace.vanillaplus.data.registryobject.VPItems;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(AbstractCow.class)
public abstract class AbstractCowMixin<T extends AbstractCow> extends MobMixin<T> {
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/TemptGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;DLjava/util/function/Predicate;Z)V"),
            index = 2)
    private Predicate<ItemStack> modifyTemptFoodCondition(Predicate<ItemStack> items) {
        return itemStack -> isBaby() ? itemStack.is(VPTags.Items.BABY_COW_FOOD) : items.test(itemStack);
    }

    @ModifyExpressionValue(method = "isFood", at = @At(value = "FIELD", target = "Lnet/minecraft/tags/ItemTags;COW_FOOD:Lnet/minecraft/tags/TagKey;",
            opcode = Opcodes.GETSTATIC))
    private TagKey<Item> modifyFood(TagKey<Item> itemTagKey) {
        return isBaby() ? VPTags.Items.BABY_COW_FOOD : itemTagKey;
    }

    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean modifyMilkCondition(boolean isBucket, @Local(name = "itemStack") ItemStack itemStack) {
        return isBucket || itemStack.is(Items.GLASS_BOTTLE);
    }

    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/item/Items;MILK_BUCKET:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC))
    private Item modifyMilkResult(Item item, @Local(name = "itemStack") ItemStack itemStack) {
        return itemStack.is(Items.GLASS_BOTTLE) ? VPItems.MILK_BOTTLE.get() : item;
    }
}
