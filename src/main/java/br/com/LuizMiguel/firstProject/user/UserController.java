package br.com.LuizMiguel.firstProject.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public UserModel create(@RequestBody UserModel userModel){
        this.userRepository.findByUsername(userModel.getUsername());

        if (userModel != null) {
            System.out.println("Usuario ja existente");
        }
        var userCreated = this.userRepository.save(userModel);
        return userCreated;
    }


}
