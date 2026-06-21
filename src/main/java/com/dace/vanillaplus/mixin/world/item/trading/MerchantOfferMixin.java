package com.dace.vanillaplus.mixin.world.item.trading;

import com.dace.vanillaplus.extension.world.item.trading.VPMerchantOffer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin implements VPMerchantOffer {
    @Shadow
    @Final
    public static final Codec<MerchantOffer> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ItemCost.CODEC.fieldOf("buy")
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).baseCostA),
                    ItemCost.CODEC.lenientOptionalFieldOf("buyB")
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).costB),
                    ItemStack.CODEC.fieldOf("sell")
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).result),
                    Codec.INT.lenientOptionalFieldOf("uses", 0)
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).uses),
                    Codec.INT.lenientOptionalFieldOf("maxUses", 4)
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).maxUses),
                    Codec.BOOL.lenientOptionalFieldOf("rewardExp", true)
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).rewardExp),
                    Codec.INT.lenientOptionalFieldOf("specialPrice", 0)
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).specialPriceDiff),
                    Codec.INT.lenientOptionalFieldOf("demand", 0)
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).demand),
                    Codec.FLOAT.lenientOptionalFieldOf("priceMultiplier", 0.0F)
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).priceMultiplier),
                    Codec.INT.lenientOptionalFieldOf("xp", 1)
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).xp),
                    Codec.BOOL.lenientOptionalFieldOf("multiplyRewardXPByCost", false)
                            .forGetter(merchantOffer -> ((MerchantOfferMixin) (Object) merchantOffer).multiplyRewardXPByCost))
            .apply(instance, MerchantOfferMixin::create));

    @Shadow
    @Final
    private ItemCost baseCostA;
    @Shadow
    @Final
    private Optional<ItemCost> costB;
    @Shadow
    @Final
    private ItemStack result;
    @Shadow
    @Final
    private boolean rewardExp;
    @Shadow
    @Final
    private float priceMultiplier;
    @Shadow
    @Final
    private int maxUses;
    @Shadow
    @Final
    private int xp;
    @Shadow
    private int uses;
    @Shadow
    private int specialPriceDiff;
    @Shadow
    private int demand;
    @Unique
    @Getter
    private boolean multiplyRewardXPByCost = false;

    @Unique
    @NonNull
    private static MerchantOffer create(ItemCost baseCostA, Optional<ItemCost> costB, ItemStack result, int uses, int maxUses, boolean rewardExp,
                                        int specialPriceDiff, int demand, float priceMultiplier, int xp, boolean multiplyRewardXPByCost) {
        MerchantOffer merchantOffer = init(baseCostA, costB, result, uses, maxUses, rewardExp, specialPriceDiff, demand, priceMultiplier, xp);
        ((MerchantOfferMixin) (Object) merchantOffer).multiplyRewardXPByCost = multiplyRewardXPByCost;

        return merchantOffer;
    }

    @Invoker("<init>")
    private static MerchantOffer init(ItemCost baseCostA, Optional<ItemCost> costB, ItemStack result, int uses, int maxUses, boolean rewardExp,
                                      int specialPriceDiff, int demand, float priceMultiplier, int xp) {
        throw new UnsupportedOperationException();
    }

    @Inject(method = "writeToStream", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/RegistryFriendlyByteBuf;writeInt(I)Lnet/minecraft/network/FriendlyByteBuf;", ordinal = 4,
            shift = At.Shift.AFTER))
    private static void writeToStream(RegistryFriendlyByteBuf output, MerchantOffer offer, CallbackInfo ci) {
        output.writeBoolean(VPMerchantOffer.cast(offer).isMultiplyRewardXPByCost());
    }

    @WrapOperation(method = "createFromStream", at = @At(value = "NEW",
            target = "(Lnet/minecraft/world/item/trading/ItemCost;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;IIIFI)Lnet/minecraft/world/item/trading/MerchantOffer;"))
    private static MerchantOffer redirectInitFromStream(ItemCost baseCostA, Optional<ItemCost> costB, ItemStack result, int _uses, int maxUses, int xp,
                                                        float priceMultiplier, int demand, Operation<MerchantOffer> original,
                                                        @Local(argsOnly = true) RegistryFriendlyByteBuf input) {
        MerchantOffer merchantOffer = original.call(baseCostA, costB, result, _uses, maxUses, xp, priceMultiplier, demand);
        ((MerchantOfferMixin) (Object) merchantOffer).multiplyRewardXPByCost = input.readBoolean();

        return merchantOffer;
    }
}
