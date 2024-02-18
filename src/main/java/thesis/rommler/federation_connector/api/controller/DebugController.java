package thesis.rommler.federation_connector.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DebugController {

    private final RestTemplate restTemplate;

    @Autowired
    public DebugController(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @GetMapping("/debug1")
    public void Debug1(){

    }

    @GetMapping("/debug2")
    public void Debug2(){

    }

    @GetMapping("/debug3")
    public void Debug3(){
        String apiUrl = "http://localhost:8080/Ping";

        // Make a GET request and handle the response
        String response = restTemplate.getForObject(apiUrl, String.class);

        // Process the response as needed
        System.out.println("Response from the API: " + response);
    }

}
