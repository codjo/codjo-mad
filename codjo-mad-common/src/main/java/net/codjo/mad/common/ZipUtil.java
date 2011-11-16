package net.codjo.mad.common;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
/**
 *
 */
public class ZipUtil {
    private ZipUtil() {
    }


    public static byte[] zip(String message) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bytes);
        ZipEntry zipEntry = new ZipEntry("z");
        try {
            out.putNextEntry(zipEntry);
            out.write(message.getBytes());
            out.closeEntry();
            return bytes.toByteArray();
        }
        finally {
            out.close();
        }
    }


    public static String unzip(byte[] zippedMessage) throws IOException {
        StringBuilder result = new StringBuilder();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zippedMessage);
        ZipInputStream in = new ZipInputStream(byteArrayInputStream);
        in.getNextEntry();

        Reader reader = new BufferedReader(new InputStreamReader(in));

        try {
            int count;
            char[] buf = new char[1024];
            while ((count = reader.read(buf)) > -1) {
                result.append(buf, 0, count);
            }
        }
        finally {
            reader.close();
        }

        return result.toString();
    }
}
