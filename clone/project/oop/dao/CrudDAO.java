
package project.oop.dao;

import java.util.*;

public interface CrudDAO<T> {
    boolean Add (T entity);
    
    List<T> SelectAll();
    
    T FindById(String id);
     
    boolean Remove (String id);
    
    boolean Update (T entity);
}
