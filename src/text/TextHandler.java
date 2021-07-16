package text;

import com.sun.istack.internal.NotNull;

/**
 * 文本处理类
 */
public class TextHandler {

    /**
     * 删除字符串中所有换行符
     */
    @NotNull
    public static String removeLineSeparator(@NotNull String string) {
        return string.replaceAll(System.lineSeparator(), "");
    }

    /**
     * 删除字符串中所有空格
     */
    @NotNull
    public static String removeSpaces(@NotNull String string) {
        return string.replaceAll(" ", "");
    }
}
