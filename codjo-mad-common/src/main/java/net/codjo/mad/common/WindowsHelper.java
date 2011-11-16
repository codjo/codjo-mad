package net.codjo.mad.common;
import java.io.File;

public class WindowsHelper {
    private WindowsHelper() {
    }


    public static void openWindowsFile(File file) {
        try {
            String executableName = "rundll32.exe url.dll,FileProtocolHandler";
            Runtime.getRuntime().exec(executableName + " " + file.getAbsolutePath());
        }
        catch (Exception e) {
            throw new RuntimeException(
                  "Erreur : le fichier " + file.getAbsolutePath() + "n'a pas pu être ouvert.", e);
        }
    }
}
