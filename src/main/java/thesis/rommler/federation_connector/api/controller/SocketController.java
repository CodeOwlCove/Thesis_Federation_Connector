package thesis.rommler.federation_connector.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thesis.rommler.federation_connector.service.FileReceiveService;

@RestController
public class SocketController {

    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);
    private FileReceiveService fileTransferService;

    @Autowired
    public SocketController(FileReceiveService fileTransferService){
        this.fileTransferService = fileTransferService;
    }

    @GetMapping("/StartSocket")
    public String StartSocket(@RequestParam int socket_port){
        try {
            fileTransferService.HandleFileTransfer(socket_port);
            return "socket_started";
        } catch (Exception e) {
            e.printStackTrace();
            return "socket_failed";
        }
    }

    @GetMapping("/CloseSocket")
    public String CloseSocket(){
        logger.info("- Closing socket.");
        try {
            fileTransferService.CloseSocket();
            return "socket_closed";
        } catch (Exception e) {
            e.printStackTrace();
            return "socket_failed";
        }
    }
}
