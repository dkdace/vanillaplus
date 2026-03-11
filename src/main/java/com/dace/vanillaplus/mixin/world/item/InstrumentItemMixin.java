package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.modifier.ItemModifier;
import com.dace.vanillaplus.extension.world.item.VPInstrument;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(InstrumentItem.class)
public abstract class InstrumentItemMixin extends ItemMixin<InstrumentItem, ItemModifier> {
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/InstrumentItem;play(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/Instrument;)V",
            shift = At.Shift.AFTER))
    private void applyEffectsToPets(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir,
                                    @Local ItemStack itemStack, @Local Optional<Holder<Instrument>> optional, @Local Instrument instrument) {
        if (level.isClientSide())
            return;

        if (itemStack.isDamageableItem())
            itemStack.hurtAndBreak(1, player, interactionHand);

        optional.flatMap(instrumentHolder -> VPInstrument.cast(instrumentHolder.value()).getDataModifier())
                .ifPresent(instrumentEffect -> {
                    float range = instrument.range() / 2;
                    AABB aabb = player.getBoundingBox().inflate(range);

                    level.getEntitiesOfClass(LivingEntity.class, aabb, entity -> entity instanceof OwnableEntity ownableEntity
                            && ownableEntity.getOwner() == player).forEach(entity -> {
                        if (aabb.distanceToSqr(entity.getBoundingBox()) < range * range)
                            entity.addEffect(new MobEffectInstance(instrumentEffect.getMobEffectInstance()));
                    });
                });
    }
}
