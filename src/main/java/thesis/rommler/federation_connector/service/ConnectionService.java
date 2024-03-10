package thesis.rommler.federation_connector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thesis.rommler.federation_connector.service.FileTransferService.FileTransferService;

import java.io.File;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.UUID;

@Service
public class ConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionService.class);
    protected ServerSocket socket_server;

    public ServerSocket GetSocket(){
        return socket_server;
    }

    /**
     * This method starts the socket server
     * @param socketPort the port on which the socket should listen
     */
    public void StartSocket(int socketPort){
        if(socket_server != null){
            if(!socket_server.isClosed()) {
                logger.info("Socket still open. Closing socket...");
                CloseSocket();
            }
        }else
            logger.info("Starting new socket on port " + socketPort + "...");

        try {
            // Create a socket server
            socket_server = new ServerSocket(socketPort);
            logger.info("Socket server started on port " + socketPort);
        }catch (BindException e){
            logger.info("Port " + socketPort + " is already in use...");
        } catch (Exception e) {
            logger.error("Error while creating socket connection.");
            e.printStackTrace();
        }
    }

    public void CloseSocket(){
        try {
            socket_server.close();

            while(!socket_server.isClosed()){
                socket_server.close();
            }

            logger.info("Is Socked closed: " + socket_server.isClosed());

            logger.info("Socket closed.");
        }catch (Exception e){
            logger.error("Error while closing socket. " + e.getMessage());
        }
    }

    public boolean isSocketRunning(){
        if(socket_server == null)
            return false;
        else return !socket_server.isClosed();
    }

}
