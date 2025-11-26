package com.dace.vanillaplus.mixin.world.item.alchemy;

import com.dace.vanillaplus.extension.VPMixin;
import com.dace.vanillaplus.extension.world.item.alchemy.VPPotion;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.OptionalInt;

@Mixin(PotionContents.class)
public abstract class PotionContentsMixin implements VPMixin<PotionContents> {
    @Shadow
    @Final
    private Optional<Holder<Potion>> potion;

    @ModifyExpressionValue(method = "getColorOr", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;getColorOptional(Ljava/lang/Iterable;)Ljava/util/OptionalInt;"))
    private OptionalInt modifyColor(OptionalInt color) {
        return potion.map(potionHolder -> VPPotion.cast(potionHolder.value()).getColor().map(OptionalInt::of).orElse(color))
                .orElse(color);
    }
}
