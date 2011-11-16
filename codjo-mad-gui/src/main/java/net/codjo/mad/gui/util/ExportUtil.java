package net.codjo.mad.gui.util;
import net.codjo.mad.gui.framework.GuiContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportUtil {
    static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    public static final String FILE_BASE_NAME = "mad.export.excel.fileBaseName";
    public static final String FILE_EXT = "mad.export.excel.fileExt";
    public static final String DECIMAL_SEPARATOR = "mad.export.excel.decimalSeparator";


    private ExportUtil() {
    }


    public static File generateFile(GuiContext context, int maxNumberOfFiles) throws IOException {
        final String baseName = (String)context.getProperty(FILE_BASE_NAME);
        final String extension = (String)context.getProperty(FILE_EXT);

        int fileNumber = 0;
        String fileName;
        File file;

        while (true) {
            fileName = generateFileName(baseName, extension, fileNumber);

            checkIfAbleToCreateFile(context, fileName, fileNumber, maxNumberOfFiles);

            file = new File(fileName);
            fileNumber++;

            if (file.exists()) {
                if (file.isFile() && file.canWrite()) {
                    try {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.close();
                    }
                    catch (FileNotFoundException ex) {
                        continue;
                    }
                    break;
                }
            }
            else {
                file.createNewFile();
                break;
            }
        }

        return file;
    }


    static String generateFileName(String baseName, String extension, int fileNumber) {
        return computeTempFileName(baseName)
               + (fileNumber == 0 ? extension : fileNumber + extension);
    }


    static String computeTempFileName(String fileName) {
        return System.getProperty(JAVA_IO_TMPDIR) + fileName;
    }


    private static void checkIfAbleToCreateFile(GuiContext context, String fileName,
                                                int fileNumber, int maxNumberOfFiles)
          throws IOException {
        if (fileNumber >= maxNumberOfFiles) {
            context.displayInfo("Cannot open the file : " + fileName);
            throw new IOException("Cannot open the file : " + fileName);
        }
    }
}
