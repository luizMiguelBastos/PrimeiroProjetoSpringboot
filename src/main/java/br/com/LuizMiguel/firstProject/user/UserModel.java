package br.com.LuizMiguel.firstProject.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity (name = "tb_users")
public class UserModel {

      @Id
      @GeneratedValue (generator = "UUID")
      private UUID id;

      private String username;
      private String name;
      private String password;

}



