package io.github.lightrailpassenger.sausage.utils;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import javax.swing.JFileChooser;

public class ReadWriteUtils {
    public static File getFileFromUI(Component parent) {
        JFileChooser chooser = new JFileChooser();
        int returnValue = chooser.showOpenDialog(parent);

        return returnValue == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    public static void writeFile(File targetFile, Charset charset, String content) throws IOException {
        Files.write(
            targetFile.toPath(),
            Collections.singletonList(content),
            charset,
            StandardOpenOption.CREATE
        );
    }

    public static String readFile(File targetFile, Charset charset) throws IOException {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(targetFile.toPath(), charset)) {
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {
                builder.append(currentLine);
                builder.append('\n');
            }
        } catch (IOException ex) {
            // TODO: Perform logging if necessary
            throw ex;
        }

        return builder.toString();
    }
}
