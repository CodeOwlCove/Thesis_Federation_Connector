package thesis.rommler.federation_connector.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static java.lang.System.exit;

@Service
public class LoginService {

    private RestTemplate restTemplate;

    public LoginService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        LogIn();
    }

    private void LogIn(){
        String apiUrl = "http://localhost:9080/login?requester_ip=localhost&requester_port=7080&socket_port=10080";

        try {
            // Make a GET request and handle the response
            String response = restTemplate.getForObject(apiUrl, String.class);

            if(response.equals("ok"))
                System.out.println("Logged in successfully.");
            else {
                System.out.println("Error while logging in.");
                exit(-1);
            }

        }catch (Exception e){
            System.out.println("Error while logging in: " + e.getMessage());
            exit(-1);
        }
    }
}
