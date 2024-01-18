package thesis.rommler.federation_connector.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import thesis.rommler.federation_connector.service.ConnectionService;


@RestController
public class ConnectionController {

    private ConnectionService userService;
    private final RestTemplate restTemplate;

    @Autowired
    public ConnectionController(ConnectionService userService, RestTemplate restTemplate){
        this.userService = userService;
        this.restTemplate = restTemplate;
        //LogIn();
    }

    private void LogIn(){
        String apiUrl = "localhost:8080/Login";

        // Make a GET request and handle the response
        String response = restTemplate.getForObject(apiUrl, String.class);

        // Process the response as needed
        System.out.println("Response from the API: " + response);
    }

    @GetMapping("/ping")
    public String getUser(){
        return "pong";
    }

    @GetMapping("/GetAllFiles")
    public String getAllFiles(){
        String apiUrl = "localhost:8080/Ping";

        // Make a GET request and handle the response
        String response = restTemplate.getForObject(apiUrl, String.class);

        // Process the response as needed
        System.out.println("Response from the API: " + response);
        return "ok";
    }
}