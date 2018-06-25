import packets.request.Request;
import packets.response.IResponsePacket;
import packets.response.Response;
import serializator.Serializer;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Arrays;

class Client {
    private final int serverPort;
    private final String host;
    private String workPath;
    private String repoName;

    Client(String host, int serverPort) {
        this.serverPort = serverPort;
        this.host = host;
        this.workPath = null;
        this.repoName = null;
    }

    private static void send(Socket socket, Request request) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        byte[] packet = Serializer.serialize(request.getPacket());
        if (packet != null) {
            outputStream.writeInt(packet.length);
            outputStream.write(packet);
            if (request.getData() != null) {
                int offset = 0;
                int percent = (int) (Math.ceil((double) request.getPacket().getDataLength() / 100));
                while (offset < request.getPacket().getDataLength()) {
                    System.out.print("  " + (int) (((double) (offset + percent) / request.getPacket().getDataLength()) * 100) + "% sended" + "\r");
                    outputStream.write(Arrays.copyOfRange(request.getData(), offset, offset + percent));
                    offset += percent;
                }
                System.out.println();
            }
        } else throw new NullPointerException();
    }

    private static Response get(Socket socket) throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        int length = inputStream.readInt();
        if (length > 0) {
            byte[] message = new byte[length];
            inputStream.readFully(message);
            IResponsePacket packet = (IResponsePacket) Serializer.deserialize(message);
            if (packet != null)
                if (packet.getDataLength() != 0) {
                    byte[] data = new byte[packet.getDataLength()];
                    inputStream.readFully(data);
                    return new Response(packet, data);
                } else {
                    return new Response(packet, null);
                }
        }
        return null;
    }

    public String getWorkPath() {
        return workPath;
    }

    public void setWorkPath(String path) {
        this.workPath = path;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String readCommand() throws IOException {
        BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
        return keyboardReader.readLine();
    }

    private static class StopException extends Exception {
    }

    void run() {
        boolean work = true;
        try {
            while (work) {
                try {
                    System.out.print("type your command: ");
                    String command = readCommand();
                    if (command.equals("stop"))
                        throw new StopException();
                    System.out.println("    data preparation...");
                    Request request = CommandParser.parseCommand(command, this);

                    Socket socket = new Socket(host, serverPort);
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(0);
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                    System.out.println("    sending request...");
                    send(socket, request);
                    System.out.println("    getting response...");
                    Response response = get(socket);

                    if (response != null) {
                        System.out.println("    executing...");
                        ICommand responseCommand = CommandFactory.handleResponse(response, workPath);
                        responseCommand.execute();
                    }

                    inputStream.close();
                    outputStream.close();
                    socket.close();
                } catch (StopException stopException) {
                    work = false;
                } catch (ConnectException exception) {
                    work = false;
                    System.out.println("server don't work");
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
