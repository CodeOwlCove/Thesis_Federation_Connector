package thesis.rommler.federation_connector.service.FileTransferService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thesis.rommler.federation_connector.service.ConnectionService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class ContractTransferService extends FileTransferService {

    private static final Logger logger = LoggerFactory.getLogger(ContractTransferService.class);

    // This is stupid, but I want to create a thread using a parameter
    private String contractName;

    public ContractTransferService(ConnectionService connectionService) {
        super(connectionService);
    }


    /**
     * This method handles the file transfer process for a specific files
     *
     * @param socketPort the port on which the socket should listen
     */
    public void HandleFileTransfer(int socketPort, String fileName) {
        CheckForFolders();

        // Create a thread for the file collection
        this.contractName = getContractNameFromFile(fileName);
        fileCollectionThread = new Thread(this::CollectContractFile);
        logger.info("Starting file collection...");
        fileCollectionThread.start();

        connectionService.StartSocket(socketPort);

        try {
            fileCollectionThread.join();
            logger.info("File collection finished.");
        } catch (Exception e) {
            logger.error("Error while waiting for file collection to finish. " + e.getMessage());
        }

        // Start listening on the socket
        logger.info("Starting socket listener on port " + socketPort + "...");
        socketThread = new Thread(this::ListenOnSocket);
        socketThread.start();
    }

    private String getContractNameFromFile(String fileNameToCheck) {
        ObjectMapper objectMapper = new ObjectMapper();

        String contractName = "";

        try {
            // Read JSON file as JsonNode
            JsonNode rootNode = objectMapper.readTree(new File("src/main/resources/Contract_Config.json"));

            // Get the "contract_links" array
            JsonNode contractLinksArray = rootNode.get("contract_links");

            // Iterate over each element in the array
            for (JsonNode contractLink : contractLinksArray) {
                if(contractLink.get("file_name").asText().equals(fileNameToCheck)){
                    contractName = contractLink.get("contract_name").asText();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Contract name: " + contractName);
        return contractName;
    }


    private void CollectContractFile(){
        ArrayList<File> fileList = new ArrayList<>();

        File folder = new File(assetFolderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getAllFiles(file, fileList);
                } else {
                    if(Arrays.asList(contractName).contains(file.getName()))
                        fileList.add(file);
                }
            }
        }

        // Compress files into a zip file
        CreateZipFile(zipFilePath, fileList);
    }

}
