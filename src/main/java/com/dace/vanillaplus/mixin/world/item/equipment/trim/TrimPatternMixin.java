package com.dace.vanillaplus.mixin.world.item.equipment.trim;

import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimPattern;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(TrimPattern.class)
public abstract class TrimPatternMixin implements VPTrimPattern {
    @Shadow
    @Final
    public static final Codec<TrimPattern> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("asset_id").forGetter(TrimPattern::assetId),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimPattern::description),
                    Codec.BOOL.fieldOf("decal").orElse(false).forGetter(TrimPattern::decal),
                    EnchantmentEffectComponents.CODEC.optionalFieldOf("effects").forGetter(trimPattern ->
                            ((TrimPatternMixin) (Object) trimPattern).enchantmentHolder.map(holder -> holder.value().effects())))
            .apply(instance, TrimPatternMixin::create));
    @Shadow
    @Final
    public static final StreamCodec<RegistryFriendlyByteBuf, TrimPattern> DIRECT_STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, TrimPattern::assetId,
            ComponentSerialization.STREAM_CODEC, TrimPattern::description,
            ByteBufCodecs.BOOL, TrimPattern::decal,
            ByteBufCodecs.optional(ByteBufCodecs.fromCodecWithRegistriesTrusted(EnchantmentEffectComponents.CODEC)), trimPattern ->
                    ((TrimPatternMixin) (Object) trimPattern).enchantmentHolder.map(holder -> holder.value().effects()),
            TrimPatternMixin::create);

    @Shadow
    @Final
    private Component description;
    @Unique
    @NonNull
    @Getter
    private Optional<Holder<Enchantment>> enchantmentHolder = Optional.empty();

    @Unique
    @NonNull
    private static TrimPattern create(Identifier assetId, Component description, boolean decal, Optional<DataComponentMap> effectMap) {
        TrimPattern trimPattern = new TrimPattern(assetId, description, decal);

        effectMap.ifPresent(dataComponentMap -> {
            Enchantment.EnchantmentDefinition enchantmentDefinition = Enchantment.definition(HolderSet.empty(), 0, 0,
                    Enchantment.constantCost(0), Enchantment.constantCost(0), 0, EquipmentSlotGroup.ARMOR);

            ((TrimPatternMixin) (Object) trimPattern).enchantmentHolder = Optional.of(Holder.direct(
                    new Enchantment(description, enchantmentDefinition, HolderSet.empty(), dataComponentMap)));
        });

        return trimPattern;
    }

    @Override
    public void applyTooltip(@NonNull Consumer<Component> componentConsumer) {
        enchantmentHolder.ifPresent(holder -> VPEnchantment.cast(holder.value()).applyTooltip(componentConsumer, description,
                1));
    }
}
