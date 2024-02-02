package thesis.rommler.federation_connector.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import thesis.rommler.federation_connector.api.answerClasses.GetFilesAnswer;
import thesis.rommler.federation_connector.service.FileReceiveService;

import static java.lang.System.exit;

@RestController
public class DebugController {

    private FileReceiveService fileTransferService;
    private final RestTemplate restTemplate;

    @Autowired
    public DebugController(FileReceiveService fileTransferService, RestTemplate restTemplate){
        this.fileTransferService = fileTransferService;
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
