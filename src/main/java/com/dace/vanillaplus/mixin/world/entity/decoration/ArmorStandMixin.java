package com.dace.vanillaplus.mixin.world.entity.decoration;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.LivingEntityMixin;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntityMixin<ArmorStand, EntityModifier.LivingEntityModifier> {
    @Shadow
    public abstract boolean showArms();

    @Shadow
    public abstract void setShowArms(boolean isEnabled);

    @Shadow
    protected abstract boolean isDisabled(EquipmentSlot equipmentSlot);

    @Inject(method = "interact", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/decoration/ArmorStand;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"),
            cancellable = true)
    private void toggleShowArmsOrSwapItems(Player player, InteractionHand interactionHand, Vec3 location,
                                           CallbackInfoReturnable<InteractionResult> cir) {
        boolean isShiftKeyDown = player.isShiftKeyDown();
        boolean isEmpty = true;

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (!isDisabled(equipmentSlot)) {
                ItemStack itemStack = getItemBySlot(equipmentSlot);
                if (!itemStack.isEmpty())
                    isEmpty = false;

                if (isShiftKeyDown) {
                    ItemStack playerItemStack = player.getItemBySlot(equipmentSlot);

                    setItemSlot(equipmentSlot, playerItemStack);
                    player.setItemSlot(equipmentSlot, itemStack);
                }
            }
        }

        boolean isDone = false;

        if (isShiftKeyDown)
            isDone = true;
        else if (isEmpty && !player.hasItemInSlot(EquipmentSlot.MAINHAND)) {
            isDone = true;
            setShowArms(!showArms());
        }

        if (isDone)
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
}
