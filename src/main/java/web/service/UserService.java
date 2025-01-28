package web.service;


import java.util.List;
import web.model.User;

public interface UserService {

    void save(User user);

    void update(User user);

    void delete(Long id);

    User findById(Long id);

    List<User> findAll();
}
