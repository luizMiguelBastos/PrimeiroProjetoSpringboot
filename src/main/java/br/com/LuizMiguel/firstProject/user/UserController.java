package br.com.LuizMiguel.firstProject.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        this.userRepository.findByUsername(userModel.getUsername());

        if (userModel != null) {
            System.out.println("Usuario ja existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario ja existe");
        }
        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }


}
