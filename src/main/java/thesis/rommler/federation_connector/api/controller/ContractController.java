package thesis.rommler.federation_connector.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractController {

    private static final Logger logger = LoggerFactory.getLogger(ContractController.class);

    public ContractController(){

    }

    @GetMapping("/GetContract")
    public String StartSelectedFilesSocket(@RequestParam int socket_port, @RequestParam String file_name){
        return "";
    }
}
