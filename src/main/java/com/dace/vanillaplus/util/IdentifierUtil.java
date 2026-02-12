package com.dace.vanillaplus.util;

import com.dace.vanillaplus.VanillaPlus;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

/**
 * {@link Identifier} 관련 기능을 제공하는 클래스.
 */
@UtilityClass
public final class IdentifierUtil {
    /**
     * 리소스 경로를 통해 식별자를 생성한다.
     *
     * @param path 리소스 경로
     * @return 식별자
     */
    @NonNull
    public static Identifier fromPath(@NonNull String path) {
        return Identifier.fromNamespaceAndPath(VanillaPlus.MODID, path);
    }

    /**
     * 리소스 키를 통해 식별자를 생성한다.
     *
     * @param resourceKey 리소스 키
     * @param <T>         데이터 타입
     * @return 식별자
     */
    @NonNull
    public static <T> Identifier fromResourceKey(@NonNull ResourceKey<T> resourceKey) {
        return fromPath(resourceKey.identifier().getPath());
    }

    /**
     * 레지스트리 값을 통해 식별자를 생성한다.
     *
     * @param registry 레지스트리
     * @param value    레지스트리 값
     * @param <T>      레지스트리 데이터 타입
     * @return 식별자
     */
    @Nullable
    public static <T> Identifier fromRegistry(@NonNull Registry<T> registry, @NonNull T value) {
        Identifier identifier = registry.getKey(value);
        return identifier == null ? null : fromPath(identifier.getPath());
    }
}
