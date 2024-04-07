package io.github.lightrailpassenger.sausage.utils;

public class StringUtil {
    public static int deriveIndentation(String content) {
        int count = 0;

        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                // Start counting
                count = 0;
            } else if (
                i > 0 &&
                (
                    content.charAt(i - 1) == '\n' ||
                    count > 0 && content.charAt(i - 1) == ' '
                ) &&
                content.charAt(i) == ' '
            ) {
                count++;
            } else if (count > 0) {
                break;
            }
        }

        return count;
    }
}
