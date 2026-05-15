package interfaces;
import java.util.List;

public interface CSVRepository<T> {
    public abstract boolean save(T entity);         
    public abstract T findById(String id);         
    public abstract List<T> findAll();           
    public abstract boolean update(T entity);        
    public abstract boolean delete(String id);       
}
