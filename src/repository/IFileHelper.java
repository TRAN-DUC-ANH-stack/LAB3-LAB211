package repository;

import java.io.IOException;
import java.util.List;

public interface IFileHelper<T> {
    List<T> load(List<String> warnings) throws IOException;
    void save(List<T> list) throws IOException;
}
