package threaddispatcher;

import java.util.ArrayList;

public class ThreadedTaskDispatcher implements ThreadedTaskListener {
    private static ThreadedTaskDispatcher INSTANCE;
    private final ArrayList<IListener> listeners;

    public static ThreadedTaskDispatcher getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ThreadedTaskDispatcher();
        return INSTANCE;
    }

    public void add(ThreadedTask task) {
        task.addThreadListener(this);
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void onThreadStart(Thread thread) {
        for (IListener listener : listeners)
            listener.onThreadAdded(thread);
    }

    @Override
    public void onThreadStop(Thread thread) {
        for (IListener listener : listeners)
            listener.onThreadRemoved(thread);
    }

    private ThreadedTaskDispatcher() {
        listeners = new ArrayList<>();
        ThreadMonitor threadMonitor = new ThreadMonitor();
        listeners.add(threadMonitor);
        add(threadMonitor);
    }
}
