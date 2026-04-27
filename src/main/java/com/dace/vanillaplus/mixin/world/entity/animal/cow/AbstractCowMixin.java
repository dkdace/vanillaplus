package com.dace.vanillaplus.mixin.world.entity.animal.cow;

import com.dace.vanillaplus.data.registryobject.VPItems;
import com.dace.vanillaplus.mixin.world.entity.MobMixin;
import com.dace.vanillaplus.world.entity.EntityModifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractCow.class)
public abstract class AbstractCowMixin<T extends AbstractCow, U extends EntityModifier.LivingEntityModifier> extends MobMixin<T, U> {
    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean modifyMilkCondition(boolean isBucket, @Local(name = "itemStack") ItemStack itemStack) {
        return isBucket || itemStack.is(Items.GLASS_BOTTLE);
    }

    @ModifyExpressionValue(method = "mobInteract", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;MILK_BUCKET:Lnet/minecraft/world/item/Item;",
            opcode = Opcodes.GETSTATIC))
    private Item modifyMilkResult(Item item, @Local(name = "itemStack") ItemStack itemStack) {
        return itemStack.is(Items.GLASS_BOTTLE) ? VPItems.MILK_BOTTLE.get() : item;
    }
}
