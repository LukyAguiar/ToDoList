package br.com.lucasaguiar.todolist.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping(value="/create")
    public ResponseEntity create(@RequestBody UserModel userModel){
      var user = this.userRepository.findByUsername(userModel.getUsername());
      if (user.size() != 0){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário ja existe");
      }
      var passwordHashred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
      userModel.setPassword(passwordHashred);

      var userCreated = this.userRepository.save(userModel);
      return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso: " + userCreated );
    }
}
