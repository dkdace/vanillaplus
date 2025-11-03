package com.dace.vanillaplus.mixin.world.entity.decoration;

import com.dace.vanillaplus.data.modifier.EntityModifier;
import com.dace.vanillaplus.mixin.world.entity.LivingEntityMixin;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntityMixin<ArmorStand, EntityModifier.LivingEntityModifier> {
    @Shadow
    public abstract void setShowArms(boolean isEnabled);

    @Shadow
    public abstract boolean showArms();

    @Inject(method = "interactAt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/decoration/ArmorStand;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"),
            cancellable = true)
    private void toggleShowArms(Player player, Vec3 interactVec, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.isShiftKeyDown() || hasItemInSlot(EquipmentSlot.MAINHAND) || hasItemInSlot(EquipmentSlot.OFFHAND))
            return;

        setShowArms(!showArms());
        cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
}
