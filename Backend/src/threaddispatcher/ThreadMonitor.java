package threaddispatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ThreadMonitor extends ThreadedTask implements IListener {
    private StringBuilder monitor = new StringBuilder();

    @Override
    public void work() {
        while (true) {
            if (Thread.interrupted()) {
                break;
            }
        }
    }

    private synchronized void print() {
        try {
            Files.write(Paths.get("threads.txt"), monitor.toString().getBytes());
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onThreadAdded(Thread thread) {
        monitor.append(thread.getName())
                .append(" ")
                .append(String.valueOf(thread.getId()))
                .append('\n');
        print();
    }

    @Override
    public void onThreadRemoved(Thread thread) {
        String currentMonitor = monitor.toString();
        monitor = new StringBuilder();
        String threadDescription = thread.getName() + " " + String.valueOf(thread.getId()) + '\n';
        monitor.append(currentMonitor.replace(threadDescription, ""));
        print();
    }
}
