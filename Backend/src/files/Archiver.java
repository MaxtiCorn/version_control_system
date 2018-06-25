package files;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Archiver {
    private static void addDirectory(ZipOutputStream zout, File fileSource) throws IOException {
        File[] files = fileSource.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirectory(zout, file);
                    continue;
                }
                FileInputStream fis = new FileInputStream(file);
                zout.putNextEntry(new ZipEntry(file.getName()));
                byte[] buffer = new byte[(int) file.length()];
                int length;
                while ((length = fis.read(buffer)) > 0)
                    zout.write(buffer, 0, length);
                zout.closeEntry();
                fis.close();
            }
    }

    public static byte[] makeArchive(File[] files) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        for (File file : files) {
            if (file.isDirectory())
                addDirectory(zipOutputStream, file);
            else {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[(int) file.length()];
                fileInputStream.read(buffer);
                zipOutputStream.write(buffer);
                fileInputStream.close();
            }
        }
        zipOutputStream.closeEntry();
        return byteArrayOutputStream.toByteArray();
    }

    public static Map<String, byte[]> readArchive(byte[] data) throws IOException {
        Map<String, byte[]> files = new HashMap<>();
        if (data != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
            ZipEntry zipEntry;
            String fileName;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                fileName = zipEntry.getName();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    byteArrayOutputStream.write(c);
                }
                byte[] file = byteArrayOutputStream.toByteArray();
                files.put(fileName, file);
            }
        }
        return files;
    }
}
