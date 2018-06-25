package files;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FolderProvider implements IDataProvider {
    private String path;

    private Path getRepoPath(String repoName) {
        return Paths.get(path, repoName);
    }

    private Path getVersionPath(String repoName, int version) {
        return Paths.get(path, repoName, String.valueOf(version));
    }

    private Path getVersionInfoPath(String repoName, int version) {
        return Paths.get(path, repoName, String.valueOf(version), "actual_files.json");
    }

    private File getRepoFolder(String repoName) {
        return new File(getRepoPath(repoName).toString());
    }

    private int getLatestVersionNum(String repoName) {
        File repo = new File(getRepoPath(repoName).toString());
        String[] list = repo.list();
        int v = 0;
        if (list != null) {
            for (String entry : list) {
                try {
                    if (Integer.valueOf(entry) > v)
                        v = Integer.valueOf(entry);
                } catch (Exception ignored) {
                }
            }
        }
        return v;
    }

    private VersionInfo getInfo(String repoName, int version) throws IOException {
        Path path = getVersionInfoPath(repoName, version);
        BufferedReader reader = new BufferedReader(new FileReader(path.toString()));
        String string;
        StringBuilder stringBuilder = new StringBuilder();
        while ((string = reader.readLine()) != null) {
            stringBuilder.append(string);
            stringBuilder.append('\n');
        }
        return new Gson().fromJson(stringBuilder.toString(), VersionInfo.class);
    }

    private void createInfo(String repoName, int version, VersionInfo info) throws IOException {
        Path path = getVersionInfoPath(repoName, version);
        Files.createFile(path);
        Gson gson = new Gson();
        FileWriter writer = new FileWriter(path.toString());
        writer.write(gson.toJson(info));
        writer.flush();
        writer.close();
    }

    private ArrayList<File> getNeededFiles(String repoName, String[] actualFiles, int version) {
        ArrayList<File> files = new ArrayList<>();
        File[] versionsFolders = getRepoFolder(repoName).listFiles();
        if (versionsFolders != null) {
            for (int i = version; i > 0; --i) {
                File vFolder = new File(getVersionPath(repoName, i).toString());
                File[] versionFiles = vFolder.listFiles();
                if (versionFiles != null) {
                    for (File vFile : versionFiles) {
                        int b = Collections.binarySearch(files, vFile, Comparator.comparing(File::getName));
                        if (Arrays.asList(actualFiles).contains(vFile.getName())
                                && b < 0
                                && !vFile.getName().equals("actual_files.json")) {
                            files.add(vFile);
                        }
                    }
                }
            }
        }
        return files;
    }

    @Override
    public boolean isRepoExists(String name) {
        return Files.exists(Paths.get(path, name));
    }

    @Override
    public void addRepo(String name) throws IOException {
        Path repoPath = Paths.get(path, name);
        if (!Files.exists(repoPath))
            Files.createDirectory(repoPath);
    }

    @Override
    public void addVersion(String repoName, byte[] archive, String[] actualFiles) throws IOException {
        int newV = getLatestVersionNum(repoName) + 1;
        Logger.log(Paths.get(getRepoPath(repoName).toString(), "log.txt"), new Date().toString() + " commit");
        Path toVersion = getVersionPath(repoName, newV);
        Files.createDirectory(toVersion);
        createInfo(repoName, newV, new VersionInfo(actualFiles));
        Map<String, byte[]> data = Archiver.readArchive(archive);
        for (Map.Entry<String, byte[]> file : data.entrySet()) {
            FileOutputStream fileOutputStream =
                    new FileOutputStream(new File(toVersion.toString(), file.getKey()));
            fileOutputStream.write(file.getValue());
            fileOutputStream.close();
        }
    }

    @Override
    public byte[] getVersion(String repoName, String[] listFiles, int version) throws IOException {
        if (Files.exists(getVersionPath(repoName, version))) {
            VersionInfo info = getInfo(repoName, version);
            ArrayList<File> neededFiles;
            ArrayList<String> intersect = new ArrayList<>();
            if (listFiles != null)
                for (String filename : listFiles) {
                    if (Arrays.asList(info.files).contains(filename))
                        intersect.add(filename);
                }
            else
                intersect.addAll(Arrays.asList(info.files));
            neededFiles = getNeededFiles(repoName, intersect.toArray(new String[0]), version);
            File[] files = neededFiles.toArray(new File[0]);
            return Archiver.makeArchive(files);
        } else return new byte[0];
    }

    @Override
    public byte[] getLastVersion(String repoName) throws IOException {
        return getVersion(repoName, null, getLatestVersionNum(repoName));
    }

    @Override
    public String getLog(String repoName) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(getRepoPath(repoName).toString(), "log.txt"));
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : allLines)
            stringBuilder.append(line).append("\n");
        return stringBuilder.toString();
    }

    public FolderProvider(String path) {
        this.path = path;
    }
}
