package packets.request;

public class PostRequestPacket implements IRequestPacket {
    private String repoName;
    private String[] actualFiles;
    private int dataLength;

    public PostRequestPacket() {
    }

    public PostRequestPacket(String repoName, String[] actualFiles, int dataLength) {
        this.repoName = repoName;
        this.actualFiles = actualFiles;
        this.dataLength = dataLength;
    }

    @Override
    public String getRepoName() {
        return repoName;
    }

    public String[] getActualFiles() {
        return actualFiles;
    }

    @Override
    public int getDataLength() {
        return dataLength;
    }
}
