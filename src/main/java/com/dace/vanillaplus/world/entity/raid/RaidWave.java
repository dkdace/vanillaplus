package com.dace.vanillaplus.world.entity.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 습격 웨이브 정보를 관리하는 클래스.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class RaidWave {
    /** JSON 코덱 */
    public static final Codec<RaidWave> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(RaiderGroup.CODEC.listOf().listOf().fieldOf("waves").forGetter(raidWave -> raidWave.raiderGroupsList))
            .apply(instance, RaidWave::new));
    /** 웨이브별 습격대원 그룹 목록 */
    private final List<List<RaiderGroup>> raiderGroupsList;

    /**
     * 지정한 웨이브 번호에 해당하는 습격대원 그룹 목록을 반환한다.
     *
     * @param wave 웨이브 번호. 1 이상의 값
     * @return 습격대원 그룹 목록
     * @throws IllegalArgumentException 인자값이 유효하지 않으면 발생
     */
    @NonNull
    @UnmodifiableView
    public List<RaiderGroup> getRaiderGroups(int wave) {
        Validate.isTrue(wave >= 1, "wave >= 1 (%d)", wave);
        return Collections.unmodifiableList(raiderGroupsList.get(Math.min(wave, raiderGroupsList.size()) - 1));
    }

    /**
     * 전체 웨이브 수를 반환한다.
     *
     * @return 전체 웨이브 수
     */
    public int getTotalWaves() {
        return raiderGroupsList.size();
    }

    /**
     * 습격대원 그룹 클래스.
     *
     * @param entityType       엔티티 타입
     * @param ridingEntityType 탑승 엔티티 타입
     * @param count            엔티티 수
     */
    public record RaiderGroup(@NonNull EntityType<?> entityType, @NonNull Optional<EntityType<?>> ridingEntityType, int count) {
        /** JSON 코덱 */
        private static final Codec<RaiderGroup> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(EntityType.CODEC.fieldOf("type").forGetter(RaiderGroup::entityType),
                        EntityType.CODEC.optionalFieldOf("vehicle").forGetter(RaiderGroup::ridingEntityType),
                        ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(RaiderGroup::count))
                .apply(instance, RaiderGroup::new));
    }
}
