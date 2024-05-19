package io.github.lightrailpassenger.sausage;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import io.github.lightrailpassenger.sausage.utils.StringUtil;

public abstract class FullLineJTextAreaTextSelectionReplacer extends TextSelectionReplacer<Integer> {
    public FullLineJTextAreaTextSelectionReplacer(TextSelectionReplacer.JTextComponentGetter getter) {
        super(getter);
    }

    protected abstract String replace(String text, int indentation);

    @Override
    public String replace(String text, Integer indentation) {
        return this.replace(text, (int)indentation);
    }

    @Override
    public Integer getMetadataFromJTextComponent(JTextComponent c) {
        return StringUtil.deriveIndentation(c.getText());
    }

    @Override
    public int rectifySelectionStart(JTextComponent tc, int offset) {
        try {
            JTextArea ta = (JTextArea)tc;
            int line = ta.getLineOfOffset(offset);
            return ta.getLineStartOffset(line);
        } catch (BadLocationException ex) {
            // Pass
            throw new InternalError(ex);
        }
    }

    @Override
    public int rectifySelectionEnd(JTextComponent tc, int offset) {
        try {
            JTextArea ta = (JTextArea)tc;
            int line = ta.getLineOfOffset(offset);
            return ta.getLineEndOffset(line);
        } catch (BadLocationException ex) {
            // Pass
            throw new InternalError(ex);
        }
    }
}
