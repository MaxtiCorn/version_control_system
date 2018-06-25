package server;

import files.IDataProvider;
import packets.request.Request;
import packets.response.Response;

public interface ICommand {
    Response execute(IDataProvider provider, Request request);
}
