package threaddispatcher;

public interface IListener {
    void onThreadAdded(Thread thread);

    void onThreadRemoved(Thread thread);
}
