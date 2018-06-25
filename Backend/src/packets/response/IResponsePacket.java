package packets.response;

import packets.IPacket;

public interface IResponsePacket extends IPacket {
    String SUCCESS_MESSAGE = "success";
    String ERROR_MESSAGE = "error";

    int SUCCESS_CODE = 0;
    int ERROR_CODE = 1;

    String getType();

    int getCode();

    String getMessage();
}
