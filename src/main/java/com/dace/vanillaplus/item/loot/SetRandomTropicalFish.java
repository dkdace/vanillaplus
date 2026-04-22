package com.dace.vanillaplus.item.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * {@link Items#TROPICAL_FISH_BUCKET} 아이템의 열대어 종류를 무작위로 지정하는 전리품 수정자 클래스.
 */
public final class SetRandomTropicalFish extends LootItemConditionalFunction {
    /** JSON 코덱 */
    public static final MapCodec<SetRandomTropicalFish> TYPE_CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).apply(instance, SetRandomTropicalFish::new));

    private SetRandomTropicalFish(@NonNull List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Override
    @NonNull
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return TYPE_CODEC;
    }

    @Override
    @NonNull
    protected ItemStack run(@NonNull ItemStack itemStack, @NonNull LootContext context) {
        List<TropicalFish.Variant> variants = TropicalFish.COMMON_VARIANTS;
        TropicalFish.Variant variant = variants.get(context.getRandom().nextInt(variants.size()));

        itemStack.set(DataComponents.TROPICAL_FISH_PATTERN, variant.pattern());
        itemStack.set(DataComponents.TROPICAL_FISH_BASE_COLOR, variant.baseColor());
        itemStack.set(DataComponents.TROPICAL_FISH_PATTERN_COLOR, variant.patternColor());

        return itemStack;
    }
}
