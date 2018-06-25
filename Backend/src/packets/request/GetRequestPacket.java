package packets.request;

public class GetRequestPacket implements IRequestPacket {
    private String type;
    private String repoName;
    private String[] files;
    private int version;

    public GetRequestPacket() {
    }

    public GetRequestPacket(String type, String repoName, String[] files, int version) {
        this.type = type;
        this.repoName = repoName;
        this.files = files;
        this.version = version;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getRepoName() {
        return repoName;
    }

    public String[] getFiles() {
        return files;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public int getDataLength() {
        return 0;
    }
}
