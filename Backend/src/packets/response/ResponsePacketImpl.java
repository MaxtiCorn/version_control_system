package packets.response;

public class ResponsePacketImpl implements IResponsePacket {
    private String type;
    private int code;
    private String message;
    private int dataLength;

    public ResponsePacketImpl() {
    }

    private ResponsePacketImpl(String type, int code, String message, int dataLength) {
        this.type = type;
        this.code = code;
        this.message = message;
        this.dataLength = dataLength;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getDataLength() {
        return dataLength;
    }

    public static class ErrorPacket extends ResponsePacketImpl {
        public ErrorPacket() {
        }

        public ErrorPacket(String type) {
            super(type, IResponsePacket.ERROR_CODE, IResponsePacket.ERROR_MESSAGE, 0);
        }
    }

    public static class SuccessPacket extends ResponsePacketImpl {
        public SuccessPacket() {
        }

        public SuccessPacket(String type, String message, int dataLength) {
            super(type, IResponsePacket.SUCCESS_CODE, message, dataLength);
        }
    }

    public static class SuccessPacketWithoutData extends ResponsePacketImpl {
        public SuccessPacketWithoutData() {
        }

        public SuccessPacketWithoutData(String message) {
            super("success", IResponsePacket.SUCCESS_CODE, message, 0);
        }
    }
}
