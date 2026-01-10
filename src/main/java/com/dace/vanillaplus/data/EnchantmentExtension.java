package com.dace.vanillaplus.data;

import com.dace.vanillaplus.VPRegistry;
import com.dace.vanillaplus.VPTags;
import com.dace.vanillaplus.VanillaPlus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 마법 부여 확장을 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = VanillaPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantmentExtension {
    /** 레지스트리 코덱 */
    public static final Codec<Holder<EnchantmentExtension>> CODEC = VPRegistry.ENCHANTMENT_EXTENSION.createRegistryCodec();
    /** JSON 코덱 */
    private static final Codec<EnchantmentExtension> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Enchantment.CODEC.fieldOf("enchantment")
                            .forGetter(enchantmentExtension -> enchantmentExtension.enchantmentHolder),
                    ExtraCodecs.intRange(0, 255).optionalFieldOf("extended_max_level", 0)
                            .forGetter(EnchantmentExtension::getMaxLevel))
            .apply(instance, EnchantmentExtension::new));

    /** 마법 부여 홀더 인스턴스 */
    private final Holder<Enchantment> enchantmentHolder;
    /** 확장된 최대 마법 부여 레벨 */
    private final int extendedMaxLevel;

    @SubscribeEvent
    private static void onDataPackNewRegistry(@NonNull DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VPRegistry.ENCHANTMENT_EXTENSION.getRegistryKey(), DIRECT_CODEC, DIRECT_CODEC);
    }

    @SubscribeEvent
    private static void onLootingLevel(@NonNull LootingLevelEvent event) {
        DamageSource damageSource = event.getDamageSource();
        if (damageSource != null && !damageSource.isDirect())
            event.setLootingLevel(0);
    }

    /**
     * 지정한 마법 부여에 해당하는 마법 부여 확장을 반환한다.
     *
     * @param enchantmentResourceKey 마법 부여 리소스 키
     * @return 마법 부여 확장. 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    public static EnchantmentExtension fromEnchantment(@NonNull ResourceKey<Enchantment> enchantmentResourceKey) {
        return VPRegistry.ENCHANTMENT_EXTENSION.getValue(enchantmentResourceKey.identifier().getPath());
    }

    /**
     * 지정한 아이템의 최대 마법 부여 레벨을 반환한다.
     *
     * @param itemStack 대상 아이템
     * @return 최대 마법 부여 레벨
     */
    public int getMaxLevel(@NonNull ItemStack itemStack) {
        int originalMaxLevel = enchantmentHolder.value().getMaxLevel();
        return extendedMaxLevel > originalMaxLevel && itemStack.is(VPTags.Items.UNLIMITED_ENCHANTABLE) ? extendedMaxLevel : originalMaxLevel;
    }

    /**
     * 마법 부여의 최대 레벨을 반환한다.
     *
     * @return 최대 마법 부여 레벨
     */
    public int getMaxLevel() {
        return Math.max(extendedMaxLevel, enchantmentHolder.value().getMaxLevel());
    }
}
