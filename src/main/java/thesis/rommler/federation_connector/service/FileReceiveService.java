    package thesis.rommler.federation_connector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.Thread.sleep;

    @Service
public class FileReceiveService {

    private static final Logger logger = LoggerFactory.getLogger(FileReceiveService.class);
    private CompletableFuture<Void> fileCollectionTask;
    private CompletableFuture<Void> socketTask;
    private ServerSocket socket_server;
    private Socket client_socket;
    private Thread fileTransferThread;
    private Thread fileCollectionThread;
    private Thread socketThread;
    private UUID rarUUID = UUID.randomUUID();

    private String zipFilePath = "src/main/resources/Outgoing/Outgoing_"+rarUUID+".zip";
    private String assetFolderPath = "src/main/resources/Assets";


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

    private static void getAllFiles(File folder, ArrayList<File> fileList) {
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

    private static void CreateZipFile(String zipFilePath, ArrayList<File> filesToZip) {

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
    private static void addToZip(File file, ZipOutputStream zos) throws IOException {
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
    private void TransferFiles(){
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
            int threshold = 0;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                bytesSent += bytesRead;
                if(bytesSent > threshold){
                    threshold += 1000000;
                    logger.info("Progress: " + bytesSent + " MB sent.");
                }
            }
            logger.info("Bytes sent: " + bytesSent);

            // Send a specific delimiter to indicate the end of the file content
            String endOfFileDelimiter = "End_Of_File";
            outputStream.write(endOfFileDelimiter.getBytes(StandardCharsets.UTF_8));

            fileInputStream.close();

            System.out.println("File sent: " + fileName);

        }catch (Exception e){
            System.out.println("Error while transferring files.");
            e.printStackTrace();
        }
    }

    /**
     * This method starts the socket server
     * @param socketPort the port on which the socket should listen
     */
    public void StartSocket(int socketPort){
        if(socket_server != null && !socket_server.isClosed()){
            logger.info("Socket still open. Closing socket...");
            CloseSocket();
        }else
            logger.info("Starting new socket on port " + socketPort + "...");

        try {
            // Create a socket server
            socket_server = new ServerSocket(socketPort);
            logger.info("Socket server started on port " + socketPort);
        } catch (Exception e) {
            System.out.println("Error while creating socket connection.");
            e.printStackTrace();
        }
    }

    /**
     * This method listens on the socket and starts the file transfer process
     */
    public void ListenOnSocket(){
        try {
            logger.info("Listening on socket on port " + socket_server.getLocalPort() + "...");
            // Listen for a connection
            client_socket = socket_server.accept();
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

        }catch (Exception e){
            logger.info("Error while listening on socket.");
            e.printStackTrace();
        }
    }

    public void CheckForFolders(){
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

        StartSocket(socketPort);

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

    public void CloseSocket(){
        try {
            new File(zipFilePath).delete();
            socket_server.close();

            while(!socket_server.isClosed()){
                socket_server.close();
            }

            socket_server = null;

            logger.info("Socket closed.");
        }catch (Exception e){
            logger.error("Error while closing socket. " + e.getMessage());
        } finally {
            DeleteSentZipFile();
        }
    }
}
