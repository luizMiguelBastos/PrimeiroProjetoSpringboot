package br.com.LuizMiguel.firstProject.user;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Users")
public class UserController {

    @GetMapping("/")
    public void create(@RequestBody UserModel userModel){
        System.out.println(userModel.getUsername());
    }


}
