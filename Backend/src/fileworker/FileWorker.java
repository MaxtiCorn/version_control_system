package fileworker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileWorker {
    private boolean recursive;
    private String path;

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean flag) {
        recursive = flag;
    }

    private Map<File, Object> workPath(File folder, IExecutable command) {
        Map<File, Object> results = new HashMap<>();
        File[] folderEntries = folder.listFiles();
        if (folderEntries != null) {
            for (File entry : folderEntries) {
                if (entry.isDirectory() && recursive) {
                    results.putAll(workPath(entry, command));
                }
                results.put(entry, command.execute(entry));
            }
        }
        return results;
    }

    public Map<File, Object> execute(IExecutable command) {
        File folder = new File(path);
        return workPath(folder, command);
    }

    public FileWorker(String path) {
        this.path = path;
    }
}
