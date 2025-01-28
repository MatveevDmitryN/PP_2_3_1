package web.dao;

import java.util.List;
import web.model.User;

public interface UserDao {

    void save(User user);

    void update(User user);

    void delete(Long id);

    User findById(Long id);

    List<User> findAll();
}
