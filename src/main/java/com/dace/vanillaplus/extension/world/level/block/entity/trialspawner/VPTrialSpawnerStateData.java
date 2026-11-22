package com.dace.vanillaplus.extension.world.level.block.entity.trialspawner;

import com.dace.vanillaplus.extension.VPMixin;
import lombok.NonNull;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;

import java.util.Optional;

/**
 * {@link TrialSpawnerStateData}를 확장하는 인터페이스.
 */
public interface VPTrialSpawnerStateData extends VPMixin<TrialSpawnerStateData> {
    @NonNull
    static VPTrialSpawnerStateData cast(@NonNull TrialSpawnerStateData object) {
        return (VPTrialSpawnerStateData) object;
    }

    /**
     * @return 시련 징조 레벨
     */
    @NonNull
    Optional<Integer> getTrialOmenLevel();

    /**
     * {@link TrialSpawnerStateData.Packed}를 확장하는 인터페이스.
     */
    interface Packed extends VPMixin<TrialSpawnerStateData.Packed> {
        @NonNull
        static Packed cast(@NonNull TrialSpawnerStateData.Packed object) {
            return (Packed) (Object) object;
        }

        /**
         * @return 시련 징조 레벨
         */
        @NonNull
        Optional<Integer> getTrialOmenLevel();

        /**
         * @param trialOmenLevel 시련 징조 레벨
         */
        void setTrialOmenLevel(@NonNull Optional<Integer> trialOmenLevel);
    }
}
