package server;

import commands.*;
import packets.request.GetRequestPacket;
import packets.request.PostRequestPacket;
import packets.request.Request;
import packets.response.Response;

public class CommandsFactory {
    public static ICommand handleRequest(Request request) {
        if (request.getPacket() instanceof GetRequestPacket) {
            GetRequestPacket requestPacket = (GetRequestPacket) request.getPacket();
            switch (requestPacket.getType()) {
                case "clone":
                    return new Clone();
                case "update":
                    return new Update();
                case "revert":
                    return new Revert();
                case "log":
                    return new Log();
                default:
                    return (provider, req) -> new Response.ErrorResponse();
            }
        }
        if (request.getPacket() instanceof PostRequestPacket) {
            return new Post();
        }
        return (provider, req) -> new Response.ErrorResponse();
    }
}
