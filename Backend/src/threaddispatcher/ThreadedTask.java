package threaddispatcher;

import java.util.ArrayList;

public abstract class ThreadedTask implements Runnable {
    private ArrayList<ThreadedTaskListener> threadedTaskListeners;

    void addThreadListener(ThreadedTaskListener listener) {
        threadedTaskListeners.add(listener);
    }

    private void onStart() {
        for (ThreadedTaskListener listener : threadedTaskListeners)
            listener.onThreadStart(Thread.currentThread());
    }

    private void onStop() {
        for (ThreadedTaskListener listener : threadedTaskListeners)
            listener.onThreadStop(Thread.currentThread());
    }

    public abstract void work();

    @Override
    public void run() {
        onStart();
        work();
        onStop();
    }

    public ThreadedTask() {
        threadedTaskListeners = new ArrayList<>();
    }
}
