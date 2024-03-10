package thesis.rommler.federation_connector.service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.logging.Logger;

import static java.lang.System.exit;

@Service
public class LoginService implements DisposableBean {

    private Logger logger = Logger.getLogger(LoginService.class.getName());
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Value("${controller.ip}") private String connectorIP;
    @Value("${controller.port}") private String connectorPort;
    @Value("${socket.port}") private String socketPort;

    private String hostIPAdress;

    public LoginService(RestTemplate restTemplate) throws UnknownHostException {
        this.restTemplate = restTemplate;

        hostIPAdress = InetAddress.getLocalHost().toString().split("/")[1];
        logger.info("- Hostname: " + hostIPAdress);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        logger.info("- Application started.");
        LogIn();
    }

    private void LogIn(){
        var serverPort = Integer.parseInt(environment.getProperty("server.port"));

        String apiUrl = "http://" + connectorIP+":"+connectorPort+"/login?requester_ip="+hostIPAdress+"&requester_port="+serverPort+"&socket_port="+socketPort;

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

    @Override
    public void destroy() throws Exception {
        logger.info("Application is shutting down...");

        var serverPort = Integer.parseInt(Objects.requireNonNull(environment.getProperty("server.port")));

        String apiUrl = "http://" + connectorIP+":"+connectorPort+"/logout?requester_ip="+hostIPAdress+"&requester_port="+serverPort;

        try {
            // Make a GET request and handle the response
            String response = restTemplate.getForObject(apiUrl, String.class);

            if(response.equals("ok"))
                logger.info("Logged out successfully.");
            else {
                logger.severe("Error while logging out.");
            }

        }catch (Exception e){
            throw new Exception("Error while logging out: " + e.getMessage());
        }
    }
}
