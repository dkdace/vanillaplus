package com.dace.vanillaplus.world.entity.monster;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntityType;
import com.dace.vanillaplus.world.entity.config.ConditionalModifierList;
import com.dace.vanillaplus.world.entity.config.ItemFunctionsModifier;
import com.dace.vanillaplus.world.entity.config.NumberModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;

/**
 * {@link Witch}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param attackCooldownMultipliers 공격 쿨타임 배수 목록
 * @param selfPotionModifiers       자신에게 사용하는 물약에 적용되는 전리품 수정자 목록
 * @param supportPotionModifiers    지원용 물약에 적용되는 전리품 수정자 목록
 * @param attackPotionModifiers     공격용 물약에 적용되는 전리품 수정자 목록
 */
public record WitchConfig(@NonNull ConditionalModifierList<NumberModifier<Float>, Float> attackCooldownMultipliers,
                          @NonNull ConditionalModifierList<ItemFunctionsModifier, ItemStack> selfPotionModifiers,
                          @NonNull ConditionalModifierList<ItemFunctionsModifier, ItemStack> supportPotionModifiers,
                          @NonNull ConditionalModifierList<ItemFunctionsModifier, ItemStack> attackPotionModifiers) {
    /** 기본값 */
    public static final WitchConfig DEFAULT = new WitchConfig(ConditionalModifierList.empty(), ConditionalModifierList.empty(),
            ConditionalModifierList.empty(), ConditionalModifierList.empty());
    /** JSON 코덱 */
    public static final Codec<WitchConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ConditionalModifierList.createCodec(NumberModifier.createCodec(ExtraCodecs.NON_NEGATIVE_FLOAT))
                            .optionalFieldOf("attack_cooldown_multipliers", DEFAULT.attackCooldownMultipliers)
                            .forGetter(WitchConfig::attackCooldownMultipliers),
                    ConditionalModifierList.createCodec(ItemFunctionsModifier.CODEC)
                            .optionalFieldOf("self_potion_modifiers", DEFAULT.selfPotionModifiers).forGetter(WitchConfig::selfPotionModifiers),
                    ConditionalModifierList.createCodec(ItemFunctionsModifier.CODEC)
                            .optionalFieldOf("support_potion_modifiers", DEFAULT.supportPotionModifiers)
                            .forGetter(WitchConfig::supportPotionModifiers),
                    ConditionalModifierList.createCodec(ItemFunctionsModifier.CODEC)
                            .optionalFieldOf("attack_potion_modifiers", DEFAULT.attackPotionModifiers)
                            .forGetter(WitchConfig::attackPotionModifiers))
            .apply(instance, WitchConfig::new));

    @NonNull
    public static WitchConfig get() {
        return VPEntityType.cast(EntityType.WITCH).getConfigComponents().getOrDefault(EntityConfigComponentTypes.WITCH, DEFAULT);
    }
}
