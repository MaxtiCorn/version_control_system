import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import files.Archiver;
import fileworker.FileWorker;
import fileworker.MD5Hasher;
import packets.response.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class CommandFactory {
    private static void calculateHashes(String workPath) throws IOException {
        FileWorker fileWorker = new FileWorker(workPath);
        fileWorker.setRecursive(true);
        Path pathToHashes = Paths.get(workPath, "hashes.json");
        if (!Files.exists(pathToHashes)) {
            Files.write(pathToHashes, "{}".getBytes());
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> fs = gson.fromJson(new FileReader(pathToHashes.toString()), type);
        Map<File, Object> hashes = fileWorker.execute(new MD5Hasher());
        for (File file : hashes.keySet()) {
            String hash = hashes.get(file).toString();
            fs.put(file.getName(), hash);
        }
        String json = gson.toJson(fs);
        Files.write(pathToHashes, json.getBytes());
    }

    private static void clearDir(String dir) {
        File d = new File(dir);
        File[] files = d.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory()) {
                    clearDir(file.getPath());
                }
                file.delete();
            }
    }

    public static ICommand handleResponse(Response response, String workPath) {
        switch (response.getPacket().getType()) {
            case "clone":
                return () -> {
                    if (!new File(workPath).exists())
                        new File(workPath).mkdir();
                    clearDir(workPath);
                    byte[] data = response.getData();
                    if (data != null) {
                        try {
                            Map<String, byte[]> arch = Archiver.readArchive(data);
                            if (arch.size() > 0)
                                for (Map.Entry<String, byte[]> file : arch.entrySet()) {
                                    FileOutputStream fileOutputStream =
                                            new FileOutputStream(new File(workPath, file.getKey()));
                                    fileOutputStream.write(file.getValue());
                                    fileOutputStream.close();
                                }
                            calculateHashes(workPath);
                        } catch (IOException ignored) {
                        }
                    }
                };
            case "update":
                return () -> {
                    File[] files = new File(workPath).listFiles();
                    if (files != null)
                        for (File file : files) {
                            file.delete();
                        }
                    byte[] data = response.getData();
                    if (data != null) {
                        try {
                            Map<String, byte[]> arch = Archiver.readArchive(data);
                            if (arch.size() > 0)
                                for (Map.Entry<String, byte[]> file : arch.entrySet()) {
                                    FileOutputStream fileOutputStream =
                                            new FileOutputStream(new File(workPath, file.getKey()));
                                    fileOutputStream.write(file.getValue());
                                    fileOutputStream.close();
                                }
                            calculateHashes(workPath);
                        } catch (IOException ignored) {
                        }
                    }
                };
            case "revert":
                return () -> {
                    File[] files = new File(workPath).listFiles();
                    if (files != null)
                        for (File file : files) {
                            file.delete();
                        }
                    byte[] data = response.getData();
                    if (data != null) {
                        try {
                            Map<String, byte[]> arch = Archiver.readArchive(data);
                            if (arch.size() > 0)
                                for (Map.Entry<String, byte[]> file : arch.entrySet()) {
                                    FileOutputStream fileOutputStream =
                                            new FileOutputStream(new File(workPath, file.getKey()));
                                    fileOutputStream.write(file.getValue());
                                    fileOutputStream.close();
                                }
                            calculateHashes(workPath);
                        } catch (IOException ignored) {
                        }
                    }
                };
            case "log":
                return () -> {
                    byte[] data = response.getData();
                    if (data != null) {
                        System.out.println(new String(data));
                    }
                };
        }
        return () -> {
        };
    }
}