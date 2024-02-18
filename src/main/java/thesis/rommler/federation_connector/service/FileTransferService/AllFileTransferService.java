package thesis.rommler.federation_connector.service.FileTransferService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thesis.rommler.federation_connector.service.ConnectionService;

import java.io.File;
import java.util.ArrayList;

@Service
public class AllFileTransferService extends FileTransferService {

    private static final Logger logger = LoggerFactory.getLogger(AllFileTransferService.class);

    // This is stupid, but I want to create a thread using a parameter
    protected String[] fileNames;

    public AllFileTransferService(ConnectionService connectionService) {
        super(connectionService);
    }


    /**
     * This method handles the file transfer process
     * @param socketPort the port on which the socket should listen
     */
    public void HandleFileTransfer(int socketPort) {
        CheckForFolders();

        // Create a thread for the file collection
        fileCollectionThread = new Thread(this::CollectFiles);
        logger.info("Starting file collection...");
        fileCollectionThread.start();

        connectionService.StartSocket(socketPort);

        try{
            fileCollectionThread.join();
            logger.info("File collection finished.");
        }catch (Exception e){
            logger.error("Error while waiting for file collection to finish. " + e.getMessage());
        }

        // Start listening on the socket
        logger.info("Starting socket listener on port " + socketPort + "...");
        socketThread = new Thread(this::ListenOnSocket);
        socketThread.start();
    }

    private void CollectFiles(){
        ArrayList<File> fileList = new ArrayList<>();

        File folder = new File(assetFolderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getAllFiles(file, fileList);
                } else {
                    fileList.add(file);
                }
            }
        }

        // Compress files into a zip file
        CreateZipFile(zipFilePath, fileList);
    }
}
