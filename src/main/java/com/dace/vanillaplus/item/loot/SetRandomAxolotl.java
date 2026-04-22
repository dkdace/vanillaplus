package com.dace.vanillaplus.item.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * {@link Items#AXOLOTL_BUCKET} 아이템의 아홀로틀 종류를 무작위로 지정하는 전리품 수정자 클래스.
 */
public final class SetRandomAxolotl extends LootItemConditionalFunction {
    /** JSON 코덱 */
    public static final MapCodec<SetRandomAxolotl> TYPE_CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).apply(instance, SetRandomAxolotl::new));

    private SetRandomAxolotl(List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Override
    @NonNull
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return TYPE_CODEC;
    }

    @Override
    @NonNull
    protected ItemStack run(@NonNull ItemStack itemStack, @NonNull LootContext lootContext) {
        itemStack.set(DataComponents.AXOLOTL_VARIANT, Axolotl.Variant.getCommonSpawnVariant(lootContext.getRandom()));
        return itemStack;
    }
}
