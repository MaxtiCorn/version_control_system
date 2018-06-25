package server;

import files.FolderProvider;
import packets.request.IRequestPacket;
import packets.request.Request;
import packets.response.Response;
import serializator.Serializer;
import threaddispatcher.ThreadedTask;
import threaddispatcher.ThreadedTaskDispatcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private FolderProvider folderProvider;

    private static void send(Socket socket, Response response) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        byte[] packet = Serializer.serialize(response.getPacket());
        if (packet != null) {
            outputStream.writeInt(packet.length);
            outputStream.write(packet);
            if (response.getData() != null)
                outputStream.write(response.getData());
        } else throw new NullPointerException();
    }

    private static Request get(Socket socket) throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        int length = inputStream.readInt();
        if (length > 0) {
            byte[] message = new byte[length];
            inputStream.readFully(message);
            IRequestPacket packet = (IRequestPacket) Serializer.deserialize(message);
            if (packet != null)
                if (packet.getDataLength() != 0) {
                    byte[] data = new byte[packet.getDataLength()];
                    inputStream.readFully(data);
                    return new Request(packet, data);
                } else return new Request(packet, null);
        }
        return null;
    }

    private void handleRequest(Socket client) {
        try {
            Request request = get(client);
            if (request != null) {
                Response resp = CommandsFactory.handleRequest(request).execute(folderProvider, request);
                send(client, resp);
            } else {
                send(client, new Response.ErrorResponse());
            }
            client.close();
        } catch (IOException ignored) {
        }
    }

    public void start(String path) {
        try {
            this.folderProvider = new FolderProvider(path);
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true) {
                Socket client = serverSocket.accept();
                ThreadedTaskDispatcher.getInstance().add(
                        new ThreadedTask() {
                            @Override
                            public void work() {
                                Server.this.handleRequest(client);
                            }
                        });

            }
        } catch (IOException ignored) {
        }
    }
}
