package com.dace.vanillaplus.util;

import lombok.experimental.UtilityClass;
import net.minecraft.util.ARGB;

/**
 * 색상 관련 기능을 제공하는 클래스.
 */
@UtilityClass
public final class ColorUtil {
    /** 각 색상 채널의 최댓값 */
    public static final int MAX_VALUE = 0xFF;

    /**
     * 투명도를 기준으로 색상을 혼합한다.
     *
     * @param baseColor  기반 색상
     * @param addedColor 투명도가 포함된 추가 색상
     * @return 최종 색상
     */
    public static int mixColor(int baseColor, int addedColor) {
        float alpha = ARGB.alphaFloat(addedColor);

        int red = ARGB.red(baseColor);
        red += (int) ((ARGB.red(addedColor) - red) * alpha);

        int green = ARGB.green(baseColor);
        green += (int) ((ARGB.green(addedColor) - green) * alpha);

        int blue = ARGB.blue(baseColor);
        blue += (int) ((ARGB.blue(addedColor) - blue) * alpha);

        return ARGB.color(MAX_VALUE, red, green, blue);
    }
}
