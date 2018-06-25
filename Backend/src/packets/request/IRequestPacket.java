package packets.request;

import packets.IPacket;

public interface IRequestPacket extends IPacket {
    @Override
    int getDataLength();

    String getRepoName();
}
