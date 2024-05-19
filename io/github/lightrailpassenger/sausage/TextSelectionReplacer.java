package io.github.lightrailpassenger.sausage;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;

public abstract class TextSelectionReplacer<T> implements Runnable {
    public static interface JTextComponentGetter {
        public JTextComponent getJTextComponent();
    }

    private JTextComponentGetter getter;

    protected TextSelectionReplacer(JTextComponentGetter getter) {
        this.getter = getter;
    }

    protected T getMetadataFromJTextComponent(JTextComponent tc) {
        return null;
    }

    protected void preReplace() {
    }

    protected int rectifySelectionStart(JTextComponent tc, int offset) {
        return offset;
    }

    protected int rectifySelectionEnd(JTextComponent tc, int offset) {
        return offset;
    }

    protected int rectifyPostSelectionStart(JTextComponent tc, int offset, T metadata) {
        return offset;
    }

    protected int rectifyPostSelectionEnd(JTextComponent tc, int offset, T metadata) {
        return offset;
    }

    public abstract String replace(String selectedText, T metadata);

    @Override
    public void run() {
        this.preReplace();

        JTextComponent tc = this.getter.getJTextComponent();
        int selectionStart = tc.getSelectionStart();
        int rectifiedSelectionStart = this.rectifySelectionStart(tc, selectionStart);
        int selectionEnd = tc.getSelectionEnd();
        int rectifiedSelectionEnd = this.rectifySelectionEnd(tc, selectionEnd);
        T metadata = this.getMetadataFromJTextComponent(tc);
        Document doc = tc.getDocument();
        int selectedLen = rectifiedSelectionEnd - rectifiedSelectionStart;

        try {
            String selectedText = doc.getText(rectifiedSelectionStart, selectedLen);
            String replaced = this.replace(selectedText, metadata);

            doc.remove(rectifiedSelectionStart, selectedLen);
            doc.insertString(rectifiedSelectionStart, replaced, SimpleAttributeSet.EMPTY);

            int postSelectionEnd = selectionEnd + replaced.length() - selectedLen;
            tc.setSelectionStart(this.rectifyPostSelectionStart(tc, selectionStart, metadata));
            tc.setSelectionEnd(this.rectifyPostSelectionEnd(tc, postSelectionEnd, metadata));
        } catch (BadLocationException ex) {
            // pass
            throw new InternalError(ex);
        }
    }
}
