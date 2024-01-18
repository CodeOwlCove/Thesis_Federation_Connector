package thesis.rommler.federation_connector.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginModule {

    @PostMapping("/login")
    public void Login(){
        //Handle Login
        System.out.println("Login");
    }
}
