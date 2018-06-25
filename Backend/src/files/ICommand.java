package files;

import packets.response.Response;

public interface ICommand {
    Response execute(IDataProvider provider);
}
