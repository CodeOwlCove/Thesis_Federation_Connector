package thesis.rommler.federation_connector.service.FileTransferService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thesis.rommler.federation_connector.service.ConnectionService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class SelectedFileTransferService extends FileTransferService {

    private static final Logger logger = LoggerFactory.getLogger(SelectedFileTransferService.class);

    // This is stupid, but I want to create a thread using a parameter
    private String[] fileNames;

    public SelectedFileTransferService(ConnectionService connectionService) {
        super(connectionService);
    }


    /**
     * This method handles the file transfer process for a specific files
     * @param socketPort the port on which the socket should listen
     */
    public void HandleFileTransfer(int socketPort, String[] fileNames) {
        CheckForFolders();

        // Create a thread for the file collection
        this.fileNames = fileNames;
        fileCollectionThread = new Thread(this::CollectSelectedFiles);
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

    private void CollectSelectedFiles(){
        ArrayList<File> fileList = new ArrayList<>();

        File folder = new File(assetFolderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getAllFiles(file, fileList);
                } else {
                    if(Arrays.asList(fileNames).contains(file.getName()))
                        fileList.add(file);
                }
            }
        }

        // Compress files into a zip file
        CreateZipFile(zipFilePath, fileList);
    }


}
