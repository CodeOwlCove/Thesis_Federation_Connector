package thesis.rommler.federation_connector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class FederationConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FederationConnectorApplication.class, args);
    }

}
