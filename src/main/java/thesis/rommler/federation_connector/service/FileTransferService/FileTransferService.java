package thesis.rommler.federation_connector.service.FileTransferService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import thesis.rommler.federation_connector.service.ConnectionService;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileTransferService {

    private static final Logger logger = LoggerFactory.getLogger(FileTransferService.class);
    protected Socket client_socket;
    protected Thread fileTransferThread;
    protected Thread fileCollectionThread;
    protected Thread socketThread;
    protected ConnectionService connectionService;
    private String rarUUID;
    protected String zipFilePath = "src/main/resources/Outgoing/Outgoing_"+rarUUID+".zip";

    @Value("${asset_folder_path}") protected String assetFolderPath;


    @Autowired
    public FileTransferService(ConnectionService connectionService){
        this.connectionService = connectionService;
        rarUUID = UUID.randomUUID().toString();
        zipFilePath = "src/main/resources/Outgoing/Outgoing_"+rarUUID+".zip";
    }


    protected static void getAllFiles(File folder, ArrayList<File> fileList) {
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
    }

    protected static void CreateZipFile(String zipFilePath, ArrayList<File> filesToZip) {
        logger.info("Creating zip file at: " + zipFilePath);

        //Create new File
        new File(zipFilePath);

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (File file : filesToZip) {
                addToZip(file, zos);
            }

            logger.info("Zip file created: " + zipFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method adds a file to the zip file
     * @param file the file to be added
     * @param zos the zip output stream
     * @throws IOException exception thrown
     */
    protected static void addToZip(File file, ZipOutputStream zos) throws IOException {
        logger.info("Add File to zip (for sending): " + file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {
            String entryName = file.getName();
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }

            zos.closeEntry();
        }
    }

    /**
     * This method sends the zip file to the federation controller
     */
    protected void TransferFiles(){
        try {
            OutputStream outputStream = client_socket.getOutputStream();

            // Send file name
            String fileName = zipFilePath;
            outputStream.write(fileName.getBytes());

            // Send file content
            FileInputStream fileInputStream = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;
            int bytesSent = 0;
            int chunksSend = 0;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                bytesSent += bytesRead;
                if(chunksSend >= 10000){
                    chunksSend = 0;
                    logger.info("Progress: " + bytesSent + " Bytes sent.");
                }
                chunksSend++;
            }
            logger.info("Bytes sent: " + bytesSent);

            // Send a specific delimiter to indicate the end of the file content
            String endOfFileDelimiter = "End_Of_File";
            outputStream.write(endOfFileDelimiter.getBytes(StandardCharsets.UTF_8));

            fileInputStream.close();

            logger.info("File sent: " + fileName);

        }catch (Exception e){
            logger.error("Error while transferring files.");
            e.printStackTrace();
        }
    }


    /**
     * This method listens on the socket and starts the file transfer process
     */
    protected void ListenOnSocket(){
        try {
            logger.info("Listening on socket on port " + connectionService.GetSocket().getLocalPort() + "...");
            // Listen for a connection
            client_socket = connectionService.GetSocket().accept();
            logger.info("Client connected on ip: " + client_socket.getInetAddress().toString());

            // Start the file transfer process
            fileTransferThread = new Thread(this::TransferFiles);
            fileTransferThread.start();

            // Wait for the file transfer to finish
            try {
                fileTransferThread.join();
            }catch (Exception e){
                logger.error("Error while waiting for file transfer to finish. " + e.getMessage());
            }

            logger.info("Deleting sent zip file...");
            DeleteSentZipFile();

        }catch (Exception e){
            logger.info("Error while listening on socket.");
            e.printStackTrace();
        }
    }

    protected void CheckForFolders(){
        File assetsFolder = new File(assetFolderPath);
        if (!assetsFolder.exists()) {
            assetsFolder.mkdirs();
        }
        File outgoingFolder = new File("src/main/resources/Outgoing");
        if (!outgoingFolder.exists()) {
            outgoingFolder.mkdirs();
        }
    }

    public void DeleteSentZipFile(){
        try {
            new File(zipFilePath).delete();
            logger.info("Sent zip file deleted.");
        }catch (Exception e){
            logger.error("Error while deleting sent zip file. " + e.getMessage());
        }
    }

    //Checks the Asset folder if it contains at least one file with one of the given names in the String[]
    public boolean CheckForFiles(String[] fileNames){
        File assetsFolder = new File(assetFolderPath);
        File[] files = assetsFolder.listFiles();
        if(files != null){
            for(File file : files){
                for(String fileName : fileNames){
                    if(file.getName().equals(fileName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
