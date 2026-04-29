package com.dace.vanillaplus.mixin.world.item.equipment.trim;

import com.dace.vanillaplus.extension.world.item.enchantment.VPEnchantment;
import com.dace.vanillaplus.extension.world.item.equipment.trim.VPTrimMaterial;
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
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(TrimMaterial.class)
public abstract class TrimMaterialMixin implements VPTrimMaterial {
    @Shadow
    @Final
    public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(MaterialAssetGroup.MAP_CODEC.forGetter(TrimMaterial::assets),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimMaterial::description),
                    EnchantmentEffectComponents.CODEC.optionalFieldOf("effects").forGetter(trimMaterial ->
                            ((TrimMaterialMixin) (Object) trimMaterial).enchantmentHolder.map(holder -> holder.value().effects())))
            .apply(instance, TrimMaterialMixin::create));
    @Shadow
    @Final
    public static final StreamCodec<RegistryFriendlyByteBuf, TrimMaterial> DIRECT_STREAM_CODEC = StreamCodec.composite(
            MaterialAssetGroup.STREAM_CODEC, TrimMaterial::assets,
            ComponentSerialization.STREAM_CODEC, TrimMaterial::description,
            ByteBufCodecs.optional(ByteBufCodecs.fromCodecWithRegistriesTrusted(EnchantmentEffectComponents.CODEC)), trimMaterial ->
                    ((TrimMaterialMixin) (Object) trimMaterial).enchantmentHolder.map(holder -> holder.value().effects()),
            TrimMaterialMixin::create);

    @Shadow
    @Final
    private Component description;
    @Unique
    @NonNull
    @Getter
    private Optional<Holder<Enchantment>> enchantmentHolder = Optional.empty();

    @Unique
    @NonNull
    private static TrimMaterial create(MaterialAssetGroup assets, Component description, Optional<DataComponentMap> effectMap) {
        TrimMaterial trimMaterial = new TrimMaterial(assets, description);

        effectMap.ifPresent(dataComponentMap -> {
            Enchantment.EnchantmentDefinition enchantmentDefinition = Enchantment.definition(HolderSet.empty(), 0, 0,
                    Enchantment.constantCost(0), Enchantment.constantCost(0), 0, EquipmentSlotGroup.ARMOR);

            ((TrimMaterialMixin) (Object) trimMaterial).enchantmentHolder = Optional.of(Holder.direct(
                    new Enchantment(description, enchantmentDefinition, HolderSet.empty(), dataComponentMap)));
        });

        return trimMaterial;
    }

    @Override
    public void applyTooltip(@NonNull Consumer<Component> componentConsumer) {
        enchantmentHolder.ifPresent(holder -> VPEnchantment.cast(holder.value()).applyTooltip(componentConsumer, description,
                1));
    }
}
