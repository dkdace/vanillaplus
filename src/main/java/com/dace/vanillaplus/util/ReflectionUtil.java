package com.dace.vanillaplus.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.asm.util.asm.ASM;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Java Reflection 관련 기능을 제공하는 클래스.
 */
@UtilityClass
public final class ReflectionUtil {
    /**
     * 지정한 패키지 경로에 있는 모든 클래스를 {@link Class#forName(String)}을 통해 불러온다.
     *
     * @param packagePath 패키지 경로
     * @param onLoadClass 클래스 로드 시 실행할 작업
     * @throws IOException 유효한 경로가 아니거나 접근할 수 없으면 발생
     */
    public static void loadClassesFromPackage(@NonNull Path packagePath, @NonNull Consumer<Class<?>> onLoadClass) throws IOException {
        try (Stream<Path> files = Files.walk(packagePath)) {
            ClassVisitor classVisitor = new ClassVisitor(ASM.API_VERSION) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    try {
                        onLoadClass.accept(Class.forName(Type.getObjectType(name).getClassName()));
                    } catch (ClassNotFoundException ex) {
                        throw new IllegalStateException("This shouldn't happen!", ex);
                    }
                }
            };

            for (Iterator<Path> iterator = files.filter(path -> path.toString().endsWith(".class")).iterator(); iterator.hasNext(); )
                try (InputStream inputStream = Files.newInputStream(iterator.next())) {
                    new ClassReader(inputStream).accept(classVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG
                            | ClassReader.SKIP_FRAMES);
                }
        }
    }
}
