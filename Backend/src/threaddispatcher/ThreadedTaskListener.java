package threaddispatcher;

public interface ThreadedTaskListener {
    void onThreadStart(Thread task);

    void onThreadStop(Thread task);
}
