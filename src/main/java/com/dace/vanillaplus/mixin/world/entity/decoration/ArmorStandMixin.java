package com.dace.vanillaplus.mixin.world.entity.decoration;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.mixin.world.entity.LivingEntityMixin;
import com.dace.vanillaplus.world.entity.decoration.ArmorStandConfig;
import lombok.NonNull;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntityMixin<ArmorStand> {
    @Shadow
    public abstract boolean showArms();

    @Shadow
    public abstract void setShowArms(boolean value);

    @Shadow
    protected abstract boolean isDisabled(EquipmentSlot slot);

    @Unique
    private void quickSwap(@NonNull Player player) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (isDisabled(equipmentSlot) || !player.isShiftKeyDown())
                continue;

            ItemStack itemStack = getItemBySlot(equipmentSlot);
            setItemSlot(equipmentSlot, player.getItemBySlot(equipmentSlot));
            player.setItemSlot(equipmentSlot, itemStack);
        }
    }

    @Unique
    private boolean isEmpty() {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values())
            if (!isDisabled(equipmentSlot) && !getItemBySlot(equipmentSlot).isEmpty())
                return false;

        return true;
    }

    @Inject(method = "interact", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/decoration/ArmorStand;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"),
            cancellable = true)
    private void toggleShowArmsOrSwapItems(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        ArmorStandConfig armorStandConfig = getConfigComponents().get(EntityConfigComponentTypes.ARMOR_STAND);
        boolean isDone = false;

        if (armorStandConfig.enableQuickSwap() && player.isShiftKeyDown()) {
            isDone = true;
            quickSwap(player);
        } else if (armorStandConfig.hasToggleableArms() && !player.hasItemInSlot(EquipmentSlot.MAINHAND) && isEmpty()) {
            isDone = true;
            setShowArms(!showArms());
        }

        if (isDone)
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
}
