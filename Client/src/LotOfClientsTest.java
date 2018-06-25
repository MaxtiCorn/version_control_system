import threaddispatcher.ThreadedTask;
import threaddispatcher.ThreadedTaskDispatcher;

public class LotOfClientsTest {
    public static class TestClient extends Client {
        private int nextCommand;
        private String[] commands;

        private TestClient(String host, int serverPort, String[] commands) {
            super(host, serverPort);
            nextCommand = 0;
            this.commands = commands;
        }

        @Override
        public String readCommand() {
            if (nextCommand < commands.length)
                return commands[nextCommand++];
            else
                return "stop";
        }
    }

    public static void main(String[] args) {
        int clientsCount = 2;
        for (int i = 0; i < clientsCount; ++i) {
            ThreadedTaskDispatcher.getInstance().add(new ThreadedTask() {
                @Override
                public void work() {
                    new TestClient("127.0.0.1", 5000,
                            new String[]{
                                    "clone C:\\Users\\letun\\Desktop\\test repo",
                                    "revert 1 -hard",
                                    "update"
                            }).run();
                }
            });
        }
    }
}
