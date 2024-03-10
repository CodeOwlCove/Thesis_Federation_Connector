package thesis.rommler.federation_connector.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import thesis.rommler.federation_connector.service.FileInformationService;

import java.util.logging.Logger;

@RestController
public class DebugController {

    private Logger logger = Logger.getLogger(DebugController.class.getName());

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
        logger.info("Response from the API: " + response);
    }

}
