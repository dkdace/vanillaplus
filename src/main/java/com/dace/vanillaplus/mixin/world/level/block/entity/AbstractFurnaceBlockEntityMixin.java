package com.dace.vanillaplus.mixin.world.level.block.entity;

import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin<T extends AbstractFurnaceBlockEntity> extends BlockEntityMixin<T> {
    @Inject(method = "burn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), cancellable = true)
    private void damageBurnedItem(NonNullList<ItemStack> items, ItemStack inputItemStack, ItemStack result, CallbackInfo ci) {
        Float smeltingDamageRatio = inputItemStack.get(VPDataComponentTypes.SMELTING_DAMAGE_RATIO.get());
        if (smeltingDamageRatio == null)
            return;

        int damage = (int) (inputItemStack.getDamageValue() + inputItemStack.getMaxDamage() * smeltingDamageRatio);
        if (damage >= inputItemStack.getMaxDamage())
            return;

        inputItemStack.setDamageValue(damage);
        ci.cancel();
    }
}
