package thesis.rommler.federation_connector.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thesis.rommler.federation_connector.service.FileReceiveService;

@RestController
public class SocketController {

    private FileReceiveService fileTransferService;

    @Autowired
    public SocketController(FileReceiveService fileTransferService){
        this.fileTransferService = fileTransferService;
    }

    @GetMapping("/StartSocket")
    public String StartSocket(@RequestParam int socket_port){
        fileTransferService.HandleFileTransfer(socket_port);
        return "Socket started.";
    }


}
