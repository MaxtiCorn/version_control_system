package commands;

import files.IDataProvider;
import packets.request.PostRequestPacket;
import packets.request.Request;
import packets.response.Response;
import server.ICommand;

import java.io.IOException;

public class Post implements ICommand {
    @Override
    public Response execute(IDataProvider provider, Request request) {
        if (request.getPacket() instanceof PostRequestPacket) {
            PostRequestPacket requestPacket = (PostRequestPacket) request.getPacket();
            try {
                if (provider.isRepoExists(requestPacket.getRepoName()) && requestPacket.getActualFiles() != null)
                    provider.addVersion(requestPacket.getRepoName(), request.getData(), requestPacket.getActualFiles());
                else
                    provider.addRepo(requestPacket.getRepoName());
                return new Response.SuccessResponseWithoutData();
            } catch (IOException e) {
                return new Response.ErrorResponse();
            }
        } else return new Response.ErrorResponse();
    }
}
