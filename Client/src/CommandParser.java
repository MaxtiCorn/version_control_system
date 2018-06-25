import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import files.Archiver;
import fileworker.FileWorker;
import fileworker.MD5Hasher;
import packets.request.GetRequestPacket;
import packets.request.PostRequestPacket;
import packets.request.Request;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CommandParser {
    public static Request parseCommand(String command, Client client) {
        String[] args = command.split(" ");
        String commandName = args[0];
        switch (commandName) {
            case "add":
                if (args.length < 2) throw new IllegalArgumentException();
                return new Request(new PostRequestPacket(args[1], null, 0), null);
            case "clone":
                if (args.length < 3) throw new IllegalArgumentException();
                client.setWorkPath(args[1]);
                client.setRepoName(args[2]);
                if (args.length == 4)
                    client.setWorkPath(Paths.get(args[1], args[2]).toString());
                return new Request(new GetRequestPacket("clone", client.getRepoName(), null, -1), null);
            case "update":
                if (client.getWorkPath() != null)
                    return new Request(new GetRequestPacket("update", client.getRepoName(), null, -1), null);
            case "commit":
                if (client.getWorkPath() != null)
                    try {
                        return commit(client);
                    } catch (IOException ignored) {
                    }
            case "revert":
                if (client.getWorkPath() != null) {
                    if (args.length > 1)
                        if (args.length > 2)
                            return new Request(new GetRequestPacket("revert", client.getRepoName(), null, Integer.valueOf(args[1])), null);
                        else {
                            String[] allFiles = new File(client.getWorkPath()).list();
                            ArrayList<String> neededFiles = new ArrayList<>(Arrays.asList(allFiles != null ? allFiles : new String[0]));
                            neededFiles.remove("hashes.json");
                            return new Request(new GetRequestPacket("revert", client.getRepoName(),
                                    neededFiles.toArray(new String[0]), Integer.valueOf(args[1])), null);
                        }
                    else
                        return new Request(new GetRequestPacket("revert", client.getRepoName(), null, -1), null);
                }
            case "log":
                if (client.getWorkPath() != null)
                    return new Request(new GetRequestPacket("log", client.getRepoName(), null, -1), null);
            default:
                throw new IllegalArgumentException("Incorrect command");
        }
    }

    private static Request commit(Client client) throws IOException {
        FileWorker fileWorker = new FileWorker(client.getWorkPath());
        fileWorker.setRecursive(true);
        Path pathToHashes = Paths.get(client.getWorkPath(), "hashes.json");
        Gson gson = new Gson();
        if (!Files.exists(pathToHashes)) {
            Files.write(pathToHashes, "{}".getBytes());
        }
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> fs = gson.fromJson(new FileReader(pathToHashes.toString()), type);
        Map<File, Object> hashes = fileWorker.execute(new MD5Hasher());
        ArrayList<File> newFiles = new ArrayList<>();
        for (File file : hashes.keySet()) {
            Object actualHash = hashes.get(file);
            String pastHash = fs.get(file.getName());
            if (((!Objects.equals(actualHash, pastHash) && actualHash != null)
                    || (actualHash == null && pastHash != null))
                    && !file.getName().equals("hashes.json")) {
                newFiles.add(file);
            }
        }
        fs.clear();
        for (File file : hashes.keySet()) {
            Object actualHash = hashes.get(file);
            fs.put(file.getName(), actualHash.toString());
        }
        String json = gson.toJson(fs);
        Files.write(pathToHashes, json.getBytes());
        String[] userFiles = new File(client.getWorkPath()).list();
        if (userFiles != null) {
            List<String> files = new ArrayList<>(Arrays.asList(userFiles));
            files.remove("hashes.json");
            userFiles = files.toArray(new String[0]);
        }
        byte[] archive = new byte[0];
        if (newFiles.size() > 0) {
            archive = Archiver.makeArchive(newFiles.toArray(new File[0]));
        }
        return new Request(new PostRequestPacket(client.getRepoName(), userFiles, archive.length), archive);
    }
}
