package com.dace.vanillaplus.world.entity.npc;

import com.dace.vanillaplus.data.registryobject.EntityConfigComponentTypes;
import com.dace.vanillaplus.extension.world.entity.VPEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Npc;

import java.util.Collections;
import java.util.Map;

/**
 * {@link Npc}의 엔티티 설정 데이터 요소 클래스.
 *
 * @param avoidEntityDistanceMap 엔티티별 기피 거리 목록
 */
public record NpcConfig(@NonNull Map<EntityType<?>, Integer> avoidEntityDistanceMap) {
    /** 기본값 */
    private static final NpcConfig DEFAULT = new NpcConfig(Collections.emptyMap());
    /** JSON 코덱 */
    public static final Codec<NpcConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.unboundedMap(EntityType.CODEC, ExtraCodecs.NON_NEGATIVE_INT)
                    .optionalFieldOf("avoid_entity_distances", DEFAULT.avoidEntityDistanceMap).forGetter(NpcConfig::avoidEntityDistanceMap))
            .apply(instance, NpcConfig::new));

    /**
     * @param entity 대상 엔티티
     * @return {@link NpcConfig}
     */
    @NonNull
    public static NpcConfig get(@NonNull Entity entity) {
        return VPEntity.cast(entity).getConfigComponents().getOrDefault(EntityConfigComponentTypes.NPC, DEFAULT);
    }
}
