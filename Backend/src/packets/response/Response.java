package packets.response;

import packets.IData;

public class Response implements IData {
    private IResponsePacket packet;
    private byte[] data;

    public Response(IResponsePacket packet, byte[] data) {
        this.packet = packet;
        this.data = data;
    }

    @Override
    public IResponsePacket getPacket() {
        return packet;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    public static class SuccessResponseWithData extends Response {
        public SuccessResponseWithData(String type, byte[] data) {
            super(new ResponsePacketImpl.SuccessPacket(type, IResponsePacket.SUCCESS_MESSAGE, data.length), data);
        }
    }

    static class ResponseWithoutData extends Response {
        public ResponseWithoutData(IResponsePacket packet) {
            super(packet, null);
        }
    }

    public static class SuccessResponseWithoutData extends Response {
        public SuccessResponseWithoutData() {
            super(new ResponsePacketImpl.SuccessPacketWithoutData(IResponsePacket.SUCCESS_MESSAGE), null);
        }
    }

    public static class ErrorResponse extends ResponseWithoutData {
        public ErrorResponse() {
            super(new ResponsePacketImpl.ErrorPacket());
        }
    }
}
