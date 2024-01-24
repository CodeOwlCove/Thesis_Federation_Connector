package thesis.rommler.federation_connector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

import static java.lang.System.exit;

@Service
public class LoginService {

    private Logger logger = Logger.getLogger(LoginService.class.getName());
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    public LoginService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        logger.info("- Application started.");
        LogIn();
    }

    private void LogIn(){
        var serverPort = Integer.parseInt(environment.getProperty("server.port"));
        var hostName = environment.getProperty("server.address", "localhost");

        String apiUrl = "http://localhost:12080/login?requester_ip="+hostName+"&requester_port="+serverPort+"&socket_port=10080";

        try {
            // Make a GET request and handle the response
            String response = restTemplate.getForObject(apiUrl, String.class);

            if(response.equals("ok"))
                logger.info("- Logged in successfully.");
            else {
                logger.severe("- Error while logging in.");
                exit(-1);
            }

        }catch (Exception e){
            logger.severe("- Error while logging in: " + e.getMessage());
            logger.severe("- Could not establish connection to Federation Controller. Shutting down..." + e.getMessage());
            exit(-1);
        }
    }
}
