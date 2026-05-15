package com.dace.vanillaplus.world.entity.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

/**
 * 아이템에 전리품 수정자를 적용하는 수정자 클래스.
 */
@EqualsAndHashCode(callSuper = true)
public final class ItemFunctionsModifier extends ConditionalModifier<ItemStack> {
    /** JSON 코덱 */
    public static final Codec<ItemFunctionsModifier> CODEC = RecordCodecBuilder.create(instance -> createCodec(instance)
            .and(LootItemFunctions.ROOT_CODEC.listOf().fieldOf("functions")
                    .forGetter(itemModifierComponent -> itemModifierComponent.lootItemFunctions))
            .apply(instance, ItemFunctionsModifier::new));
    /** 전리품 수정자 목록 */
    @NonNull
    private final List<LootItemFunction> lootItemFunctions;

    private ItemFunctionsModifier(@NonNull Optional<LootItemCondition> condition, @NonNull List<LootItemFunction> lootItemFunctions) {
        super(condition);
        this.lootItemFunctions = lootItemFunctions;
    }

    @Override
    @NonNull
    protected ItemStack run(@NonNull ItemStack value, @NonNull LootContext lootContext) {
        for (LootItemFunction lootItemFunction : lootItemFunctions) {
            value = lootItemFunction.apply(value, lootContext);
            if (value.isEmpty())
                break;
        }

        return value;
    }
}
