package io.github.lightrailpassenger.sausage.indent;

public class TextIndentUtil {
    public static String repeatSpace(int count) {
        if (count <= 0) {
            throw new IndexOutOfBoundsException("count cannot be non-negative");
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            builder.append(' ');
        }

        return builder.toString();
    }

    public static String indentString(String str, int spaceCount) {
        StringBuilder builder = new StringBuilder();
        String indentation = null;

        boolean shouldIndent = true;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (shouldIndent) {
                shouldIndent = false;

                if (indentation == null) {
                    indentation = TextIndentUtil.repeatSpace(spaceCount);
                }

                builder.append(indentation);
            }

            builder.append(c);

            if (c == '\n') {
                shouldIndent = true;
            }
        }

        return builder.toString();
    }

    public static String unindentString(String str, int spaceCount) {
        StringBuilder builder = new StringBuilder();
        String indentation = TextIndentUtil.repeatSpace(spaceCount);
        String[] lines = str.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.startsWith(indentation)) {
                builder.append(line.substring(spaceCount));
            } else {
                builder.append(line);
            }

            builder.append('\n');
        }

        return builder.toString();
    }
}
