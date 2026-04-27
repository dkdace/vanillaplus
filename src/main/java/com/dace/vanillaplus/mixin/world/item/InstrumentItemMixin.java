package com.dace.vanillaplus.mixin.world.item;

import com.dace.vanillaplus.data.registryobject.VPDataComponentTypes;
import com.dace.vanillaplus.extension.VPModifiableData;
import com.dace.vanillaplus.extension.world.item.VPInstrument;
import com.dace.vanillaplus.network.NetworkManager;
import com.dace.vanillaplus.network.client.StopSoundPacket;
import com.dace.vanillaplus.world.item.ItemModifier;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.NonNull;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(InstrumentItem.class)
public abstract class InstrumentItemMixin extends ItemMixin<InstrumentItem, ItemModifier.InstrumentModifier> {
    @Shadow
    private static Optional<Holder<Instrument>> getInstrument(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Unique
    @NonNull
    private static Optional<Integer> getUseDuration(@NonNull ItemStack itemStack) {
        return VPModifiableData.getDataModifier(itemStack.getItem(), ItemModifier.InstrumentModifier.class)
                .flatMap(ItemModifier.InstrumentModifier::getUseDuration);
    }

    @Unique
    private static void applyEffects(@NonNull Instrument instrument, @NonNull Level level, @NonNull LivingEntity user) {
        VPInstrument.cast(instrument).getMobEffectInstance().ifPresent(mobEffectInstance -> {
            float range = instrument.range() / 2;
            AABB aabb = user.getBoundingBox().inflate(range);

            level.getEntitiesOfClass(LivingEntity.class, aabb, entity -> entity instanceof OwnableEntity ownableEntity
                    && ownableEntity.getOwner() == user).forEach(entity -> {
                if (aabb.distanceToSqr(entity.getBoundingBox()) < range * range)
                    entity.addEffect(new MobEffectInstance(mobEffectInstance));
            });
        });
    }

    @Unique
    private static void damageItem(@NonNull ItemStack itemStack, @NonNull LivingEntity user, @NonNull InteractionHand interactionHand) {
        if (itemStack.isDamageableItem())
            itemStack.hurtAndBreak(1, user, interactionHand);
    }

    @WrapWithCondition(method = "play", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static boolean redirectPlaySound(Level level, Entity except, Entity sourceEntity, SoundEvent sound, SoundSource source, float volume,
                                             float pitch, @Local(argsOnly = true) Player player, @Local(argsOnly = true) Instrument instrument) {
        ItemStack itemStack = player.getUseItem();
        if (getUseDuration(itemStack).isEmpty())
            return true;

        if (level instanceof ServerLevel serverLevel) {
            long seed = serverLevel.getRandom().nextLong();

            itemStack.set(VPDataComponentTypes.SEED.get(), seed);
            serverLevel.playSeededSound(null, sourceEntity, instrument.soundEvent(), source, volume, pitch, seed);
        }

        return false;
    }

    @ModifyReturnValue(method = "getUseDuration", at = @At("RETURN"))
    private static int modifyUseDuration(int duration, @Local(argsOnly = true) ItemStack itemStack) {
        return getUseDuration(itemStack).orElse(duration);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        if (getUseDuration(stack).isEmpty())
            return;

        if (entity.level() instanceof ServerLevel serverLevel) {
            Long seed = stack.get(VPDataComponentTypes.SEED.get());
            if (seed != null)
                NetworkManager.sendToLevel(new StopSoundPacket(SoundSource.RECORDS, seed), serverLevel);
        }

        if (entity instanceof Player player) {
            getInstrument(stack).ifPresent(instrumentHolder -> {
                player.getCooldowns().addCooldown(stack, (int) (instrumentHolder.value().useDuration() * 20.0));
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            });
        }

        damageItem(stack, entity, entity.getUsedItemHand());
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && getUseDuration(itemStack).isPresent()) {
            itemStack.remove(VPDataComponentTypes.SEED.get());
            getInstrument(itemStack).ifPresent(instrumentHolder -> applyEffects(instrumentHolder.value(), level, entity));
        }

        return itemStack;
    }

    @Inject(method = "use", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/InstrumentItem;play(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/Instrument;)V",
            shift = At.Shift.AFTER), cancellable = true)
    private void cancelPlaySoundIfNoDuration(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir,
                                             @Local(name = "itemStack") ItemStack itemStack) {
        if (getUseDuration(itemStack).isPresent())
            cir.setReturnValue(InteractionResult.CONSUME);
    }

    @Inject(method = "use", at = @At(value = "RETURN", ordinal = 0))
    private void applyEffectsIfNoDuration(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir,
                                          @Local(name = "itemStack") ItemStack itemStack, @Local(name = "instrument") Instrument instrument) {
        if (!level.isClientSide())
            applyEffects(instrument, level, player);

        damageItem(itemStack, player, hand);
    }
}
