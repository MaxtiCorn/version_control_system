package packets.request;

import packets.IData;

public class Request implements IData {
    private IRequestPacket packet;
    private byte[] data;

    public Request(IRequestPacket packet, byte[] data) {
        this.packet = packet;
        this.data = data;
    }

    @Override
    public IRequestPacket getPacket() {
        return packet;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
