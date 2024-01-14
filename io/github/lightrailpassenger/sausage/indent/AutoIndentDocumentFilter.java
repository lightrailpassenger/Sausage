package io.github.lightrailpassenger.sausage.indent;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.JTextArea;

public class AutoIndentDocumentFilter extends DocumentFilter {
    private final JTextArea textArea;
    private final Set<Character> indentStartSymbols;
    private final Set<Character> indentEndSymbols;
    private final String indent;

    public AutoIndentDocumentFilter(JTextArea textArea, Collection<Character> indentStartSymbols, Collection<Character> indentEndSymbols, String indent) {
        super();
        this.textArea = textArea;
        this.indentStartSymbols = new HashSet<Character>(indentStartSymbols);
        this.indentEndSymbols = new HashSet<Character>(indentEndSymbols);
        this.indent = indent;
    }

    public AutoIndentDocumentFilter(JTextArea textArea, Collection<Character> indentStartSymbols, Collection<Character> indentEndSymbols, int count, char indentChar) {
        this(textArea, indentStartSymbols, indentEndSymbols, AutoIndentDocumentFilter.createIndentString(count, indentChar));
    }

    private static String createIndentString(int count, char indentChar) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            builder.append(indentChar);
        }

        return builder.toString();
    }

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet set) throws BadLocationException {
        this.indentInsertion(fb, offset, string, set);
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet set) throws BadLocationException {
        fb.remove(offset, length);
        this.indentInsertion(fb, offset, string, set);
    }

    private void indentInsertion(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet set) throws BadLocationException {
        boolean isNewLine = false;

        if ((isNewLine = "\n".equals(string)) || this.indentEndSymbols.contains(string.charAt(0))) {
            int caret = this.textArea.getCaretPosition();
            int lineNumber = this.textArea.getLineOfOffset(caret);
            int lineStart = this.textArea.getLineStartOffset(lineNumber);
            String line = fb.getDocument().getText(lineStart, caret - lineStart);
            int whitespaceEnd = line.length();

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                if (c != ' ' && c != '\t') {
                    whitespaceEnd = i;
                    break;
                }
            }

            String previousFullIndent = line.substring(0, whitespaceEnd);

            if (!isNewLine) {
                int index = previousFullIndent.indexOf(indent);

                if (index >= 0) {
                    fb.remove(lineStart + index, indent.length());
                }

                super.insertString(fb, offset - indent.length(), string, set);
            } else if (this.indentStartSymbols.contains(line.charAt(line.length() - 1))) {
                super.insertString(fb, offset, string + previousFullIndent + this.indent, set);
            } else {
                super.insertString(fb, offset, string + previousFullIndent, set);
            }
        } else {
            super.insertString(fb, offset, string, set);
        }
    }
}
