package thesis.rommler.federation_connector.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thesis.rommler.federation_connector.service.ConnectionService;
import thesis.rommler.federation_connector.service.FileTransferService.AllFileTransferService;
import thesis.rommler.federation_connector.service.FileTransferService.SelectedFileTransferService;

import java.util.Arrays;

@RestController
public class SocketController {

    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);
    private SelectedFileTransferService selectedFileTransferService;
    private AllFileTransferService allFileTransferService;
    private ConnectionService connectionService;

    @Autowired
    public SocketController(SelectedFileTransferService selectedFileTransferService, AllFileTransferService allFileTransferService,
                            ConnectionService connectionService){
        this.selectedFileTransferService = selectedFileTransferService;
        this.allFileTransferService = allFileTransferService;
        this.connectionService = connectionService;
    }

    @GetMapping("/StartAllFilesSocket")
    public String StartSocket(@RequestParam int socket_port){
        try {
            allFileTransferService.HandleFileTransfer(socket_port);
            return "socket_started";
        } catch (Exception e) {
            e.printStackTrace();
            return "socket_failed";
        }
    }

    @GetMapping("/StartSelectedFilesSocket")
    public String StartSelectedFilesSocket(@RequestParam int socket_port, @RequestParam String[] file_name){
        logger.info("FileNames: " + Arrays.toString(file_name));

        try {
            selectedFileTransferService.HandleFileTransfer(socket_port, file_name);
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

            if(connectionService.isSocketRunning())
                connectionService.CloseSocket();

            logger.info("- Socket closed.");

            return "socket_closed";
        } catch (Exception e) {

            logger.error("- Error while closing socket.");
            e.printStackTrace();
            return "socket_failed";
        }
    }
}
