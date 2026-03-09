package com.dace.vanillaplus.util;

import com.dace.vanillaplus.VanillaPlus;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;

/**
 * {@link Identifier} 관련 기능을 제공하는 클래스.
 */
@UtilityClass
public final class IdentifierUtil {
    /** 경로 구분자 */
    private static final String PATH_SEPARATOR = "/";

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
     * 두 식별자를 이어붙여 새로운 식별자를 생성한다.
     *
     * @param first  첫번째 식별자
     * @param second 두번째 식별자
     * @return 식별자
     * @see IdentifierUtil#concat(Identifier...)
     */
    @NonNull
    public static Identifier concat(@NonNull Identifier first, @NonNull Identifier second) {
        return fromPath(first.getPath() + PATH_SEPARATOR + second.getPath());
    }

    /**
     * 지정한 식별자들을 이어붙여 새로운 식별자를 생성한다.
     *
     * @param identifiers 식별자 목록
     * @return 식별자
     * @see IdentifierUtil#concat(Identifier, Identifier)
     */
    @NonNull
    public static Identifier concat(@NonNull Identifier @NonNull ... identifiers) {
        StringJoiner stringJoiner = new StringJoiner(PATH_SEPARATOR);
        for (Identifier identifier : identifiers)
            stringJoiner.add(identifier.getPath());

        return fromPath(stringJoiner.toString());
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
