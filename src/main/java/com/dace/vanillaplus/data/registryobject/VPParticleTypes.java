package com.dace.vanillaplus.data.registryobject;

import com.dace.vanillaplus.data.StaticRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 모드에서 사용하는 입자 타입을 관리하는 클래스.
 */
@UtilityClass
public final class VPParticleTypes {
    private static final DeferredRegister<ParticleType<?>> REGISTRY = StaticRegistry.createDeferredRegister(Registries.PARTICLE_TYPE);

    public static final RegistryObject<SimpleParticleType> SULFUR_ASH = create("sulfur_ash", false);
    public static final RegistryObject<SimpleParticleType> NOXIOUS_GAS_BURNING = create("noxious_gas_burning", false);
    public static final RegistryObject<SimpleParticleType> NOXIOUS_GAS_CLOUD_BURNING = create("noxious_gas_cloud_burning", false);

    @NonNull
    private static RegistryObject<SimpleParticleType> create(@NonNull String name, boolean overrideLimiter) {
        return REGISTRY.register(name, () -> new SimpleParticleType(overrideLimiter));
    }
}
