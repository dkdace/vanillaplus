package com.dace.vanillaplus.mixin.world.item.alchemy;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.effect.VPMobEffect;
import com.dace.vanillaplus.extension.world.item.alchemy.VPPotion;
import com.dace.vanillaplus.world.MobEffectValues;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

@Mixin(PotionContents.class)
public abstract class PotionContentsMixin implements VPMixin<PotionContents> {
    @Unique
    private static final int COLOR_BENEFICIAL = ARGB.color(127, 127, 255);
    @Unique
    private static final int COLOR_NEUTRAL = ARGB.color(204, 204, 204);
    @Unique
    private static final int COLOR_HARMFUL = ARGB.color(255, 127, 127);

    @Shadow
    @Final
    private Optional<Holder<Potion>> potion;

    @Redirect(method = "addPotionTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;",
            ordinal = 0))
    private static MutableComponent modifyMobEffectColor(MutableComponent line, ChatFormatting format,
                                                         @Local(name = "mobEffect") Holder<MobEffect> mobEffect) {
        return line.withColor(switch (mobEffect.value().getCategory()) {
            case BENEFICIAL -> COLOR_BENEFICIAL;
            case NEUTRAL -> COLOR_NEUTRAL;
            case HARMFUL -> COLOR_HARMFUL;
        });
    }

    @Redirect(method = "addPotionTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private static boolean removeAttributeDisplay(List<Pair<Holder<Attribute>, AttributeModifier>> modifiers) {
        return true;
    }

    @Inject(method = "addPotionTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private static void addEffectTooltip(Iterable<MobEffectInstance> effects, Consumer<Component> lines, float durationScale, float tickrate,
                                         CallbackInfo ci, @Local(name = "effect") MobEffectInstance effect) {
        MobEffect mobEffect = effect.getEffect().value();

        VPMobEffect.cast(mobEffect).getDataModifier().map(MobEffectValues::getValues).ifPresent(describeds ->
                describeds.forEach(described -> described.applyTooltip(lines, mobEffect.getDisplayName(), effect.getAmplifier() + 1)));

        mobEffect.createModifiers(effect.getAmplifier(), (attributeHolder, attributeModifier) ->
                ItemAttributeModifiers.Display.attributeModifiers().apply(component ->
                        lines.accept(CommonComponents.space().append(component)), null, attributeHolder, attributeModifier));
    }

    @ModifyExpressionValue(method = "getColorOr", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;getColorOptional(Ljava/lang/Iterable;)Ljava/util/OptionalInt;"))
    private OptionalInt modifyColor(OptionalInt color) {
        return potion.flatMap(potionHolder -> VPPotion.cast(potionHolder.value()).getDataModifier()
                .flatMap(potionModifier -> potionModifier.getColor().map(OptionalInt::of))).orElse(color);
    }
}
