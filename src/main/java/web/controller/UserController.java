package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.service.UserService;
import web.model.User;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }


    @PostMapping("/new")
    public String saveOrUpdateUser(@RequestParam(value = "id", required = false) Long id,
                                   @RequestParam("name") String name,
                                   @RequestParam("email") String email,
                                   @RequestParam("age") int age) {
        User user;
        if (id == null || id == 0) {
            user = new User(name, email, age);
            userService.save(user);
        } else {
            user = userService.findById(id);
            if (user != null) {
                user.setName(name);
                user.setEmail(email);
                user.setAge(age);
                userService.update(user);
            }
        }

        return "redirect:/users";
    }


    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/users";
    }
}