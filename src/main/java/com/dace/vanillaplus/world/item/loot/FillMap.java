package com.dace.vanillaplus.world.item.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * 현재 위치를 기준으로 작성된 지도 아이템을 반환하는 전리품 수정자 클래스.
 */
public final class FillMap extends LootItemConditionalFunction {
    /** JSON 코덱 */
    public static final MapCodec<FillMap> TYPED_CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).apply(instance, FillMap::new));

    private FillMap(List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Override
    @NonNull
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return TYPED_CODEC;
    }

    @Override
    @NonNull
    public ItemStack run(@NonNull ItemStack itemStack, @NonNull LootContext lootContext) {
        BlockPos blockPos = BlockPos.containing(lootContext.getParameter(LootContextParams.ORIGIN));
        return MapItem.create(lootContext.getLevel(), blockPos.getX(), blockPos.getZ(), (byte) 0, true, false);
    }
}
