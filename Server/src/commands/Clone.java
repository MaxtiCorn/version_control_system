package commands;

import files.IDataProvider;
import packets.request.GetRequestPacket;
import packets.request.Request;
import packets.response.Response;
import server.ICommand;

import java.io.IOException;

public class Clone implements ICommand {
    @Override
    public Response execute(IDataProvider provider, Request request) {
        if (request.getPacket() instanceof GetRequestPacket) {
            GetRequestPacket requestPacket = (GetRequestPacket) request.getPacket();
            if (provider.isRepoExists(requestPacket.getRepoName())) {
                try {
                    byte[] responseData = provider.getLastVersion(requestPacket.getRepoName());
                    return new Response.SuccessResponseWithData(requestPacket.getType(), responseData);
                } catch (IOException e) {
                    return new Response.ErrorResponse();
                }
            } else {
                return new Response.ErrorResponse();
            }
        } else return new Response.ErrorResponse();
    }
}
