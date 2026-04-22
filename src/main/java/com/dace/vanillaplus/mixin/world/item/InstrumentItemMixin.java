package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.extension.world.item.VPInstrument;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.packet.StopSoundPacketHandler;
import com.dace.vanillaplus.world.item.ItemModifier;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(InstrumentItem.class)
public abstract class InstrumentItemMixin extends ItemMixin<InstrumentItem, ItemModifier.InstrumentModifier> {
    @Redirect(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void redirectPlaySound(Level level, Entity entity, Entity sourceEntity, SoundEvent soundEvent, SoundSource soundSource,
                                          float volume, float pitch, @Local(argsOnly = true) Player player,
                                          @Local(argsOnly = true) Instrument instrument) {
        if (!(level instanceof ServerLevel serverLevel) || VPInstrument.cast(instrument).getDataModifier().isEmpty())
            return;

        ItemStack itemStack = player.getUseItem();
        long seed = serverLevel.getRandom().nextLong();

        itemStack.set(VPDataComponentTypes.SEED.get(), seed);
        serverLevel.playSeededSound(null, sourceEntity, instrument.soundEvent(), soundSource, volume, pitch, seed);
    }

    @ModifyReturnValue(method = "getUseDuration", at = @At("RETURN"))
    private static int modifyUseDuration(int duration, @Local(argsOnly = true) ItemStack itemStack) {
        return VPModifiableData.getDataModifier(itemStack.getItem(), ItemModifier.InstrumentModifier.class)
                .map(ItemModifier.InstrumentModifier::getUseDuration)
                .orElse(duration);
    }

    @Shadow
    @UnknownNullability
    private static Optional<Holder<Instrument>> getInstrument(ItemStack itemStack) {
        return Optional.empty();
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        getInstrument(stack).ifPresent(instrumentHolder -> {
            if (entity.level() instanceof ServerLevel serverLevel) {
                Long seed = stack.get(VPDataComponentTypes.SEED.get());
                if (seed != null)
                    NetworkManager.sendToLevel(new StopSoundPacketHandler(SoundSource.RECORDS, seed), serverLevel);
            }

            if (entity instanceof Player player) {
                player.getCooldowns().addCooldown(stack, (int) (instrumentHolder.value().useDuration() * 20.0));
                player.awardStat(Stats.ITEM_USED.get(getThis()));
            }

            if (stack.isDamageableItem())
                stack.hurtAndBreak(1, entity, entity.getUsedItemHand());
        });
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide())
            getInstrument(itemStack).ifPresent(instrumentHolder -> {
                itemStack.remove(VPDataComponentTypes.SEED.get());

                Instrument instrument = instrumentHolder.value();

                VPInstrument.cast(instrument).getDataModifier().ifPresent(instrumentEffect -> {
                    float range = instrument.range() / 2;
                    AABB aabb = livingEntity.getBoundingBox().inflate(range);

                    level.getEntitiesOfClass(LivingEntity.class, aabb, entity -> entity instanceof OwnableEntity ownableEntity
                            && ownableEntity.getOwner() == livingEntity).forEach(entity -> {
                        if (aabb.distanceToSqr(entity.getBoundingBox()) < range * range)
                            entity.addEffect(new MobEffectInstance(instrumentEffect.getMobEffectInstance()));
                    });
                });
            });

        return itemStack;
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/InstrumentItem;play(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/Instrument;)V",
            shift = At.Shift.AFTER), cancellable = true)
    private void cancelAfterPlaySound(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir,
                                      @Local Instrument instrument) {
        if (VPInstrument.cast(instrument).getDataModifier().isPresent())
            cir.setReturnValue(InteractionResult.CONSUME);
    }
}
