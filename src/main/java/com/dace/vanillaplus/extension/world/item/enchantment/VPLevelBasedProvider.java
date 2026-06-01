package com.dace.vanillaplus.extension.world.item.enchantment;

import lombok.NonNull;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 레벨 기반 값 ({@link LevelBasedValue})를 가진 요소를 나타내는 인터페이스.
 */
public interface VPLevelBasedProvider {
    @NonNull
    static List<LevelBasedValue> getFromList(@NonNull List<?> list) {
        return list.stream()
                .filter(VPLevelBasedProvider.class::isInstance)
                .flatMap(enchantmentEntityEffect -> ((VPLevelBasedProvider) enchantmentEntityEffect).getLevelBasedValues().stream())
                .toList();
    }

    @NonNull
    static List<LevelBasedValue> getFromConditionalEffect(@NonNull Object effect, @NonNull Optional<LootItemCondition> condition) {
        List<LevelBasedValue> effectValues = null;
        if (effect instanceof VPLevelBasedProvider vpLevelBasedProvider)
            effectValues = vpLevelBasedProvider.getLevelBasedValues();

        List<LevelBasedValue> conditionValues = condition
                .filter(VPLevelBasedProvider.class::isInstance)
                .map(lootItemCondition -> ((VPLevelBasedProvider) lootItemCondition).getLevelBasedValues())
                .orElse(null);

        if (effectValues != null && conditionValues != null) {
            conditionValues = new ArrayList<>(conditionValues);
            conditionValues.addAll(effectValues);

            return conditionValues;
        }

        if (effectValues != null)
            return effectValues;
        if (conditionValues != null)
            return conditionValues;

        return Collections.emptyList();
    }

    /**
     * @return 레벨 기반 값 목록
     */
    @NonNull
    @UnmodifiableView
    default List<LevelBasedValue> getLevelBasedValues() {
        return Collections.emptyList();
    }
}
