package fileworker;

import java.io.File;

public interface IExecutable<T> {
    T execute(File file);
}
