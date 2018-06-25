package files;

import java.io.IOException;

public interface IDataProvider {
    boolean isRepoExists(String name);

    void addRepo(String name) throws IOException;

    void addVersion(String repoName, byte[] archive, String[] actualFiles) throws IOException;

    byte[] getVersion(String repoName, String[] files, int version) throws IOException;

    byte[] getLastVersion(String repoName) throws IOException;

    String getLog(String repoName) throws IOException;
}
