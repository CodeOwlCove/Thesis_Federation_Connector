    package thesis.rommler.federation_connector.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileReceiveService {

    private ExecutorService executorService;
    private CompletableFuture<Void> fileCollectionTask;
    private CompletableFuture<Void> socketTask;
    private ServerSocket socket_server;
    private Socket client_socket;


    private void CollectFiles(){
        String sourceFolderPath = "F:\\Masterarbeit_Gits\\federation_connector\\Assets_Outgoing";
        String zipFilePath = "F:\\Masterarbeit_Gits\\federation_connector\\Asset_Zip\\test.zip";

        ArrayList<File> fileList = new ArrayList<>();

        File folder = new File(sourceFolderPath);
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

        //Delete Old File
        if(new File(zipFilePath).exists()){
            new File(zipFilePath).delete();
        }

        //Create new File
        new File(zipFilePath);

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (File file : filesToZip) {
                addToZip(file, zos);
            }

            System.out.println("Zip file created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void TransferFiles(){
        try {
            OutputStream outputStream = client_socket.getOutputStream();

            // Send file name
            String fileName = "F:\\Masterarbeit_Gits\\federation_connector\\Asset_Zip\\test.zip";
            outputStream.write(fileName.getBytes());

            // Send file content
            FileInputStream fileInputStream = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;
            int bytesSent = 0;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                bytesSent += bytesRead;
                System.out.println("Progress: " + bytesSent + " bytes sent");
            }

            // Send a specific delimiter to indicate the end of the file content
            String endOfFileDelimiter = "END_OF_FILE";
            outputStream.write(endOfFileDelimiter.getBytes(StandardCharsets.UTF_8));

            fileInputStream.close();
            socket_server.close();

            System.out.println("File sent: " + fileName);
        }catch (Exception e){
            System.out.println("Error while transferring files.");
            e.printStackTrace();
        }
    }

    public void StartSocket(int socketPort){
        try {
            socket_server = new ServerSocket(socketPort);
            System.out.println("Socket listening on port " + socketPort + ".");
            client_socket = socket_server.accept();
            System.out.println("Client connected on ip: " + client_socket.getInetAddress().toString());
        } catch (Exception e) {
            System.out.println("Error while creating socket connection.");
            e.printStackTrace();
        }
    }

    public void HandleFileTransfer(int socketPort) {
        executorService = Executors.newFixedThreadPool(2);
        fileCollectionTask = CompletableFuture.runAsync(() -> {
            CollectFiles();
            System.out.println("Files Collected");
        }, executorService);

        socketTask = CompletableFuture.runAsync(() -> {
            StartSocket(socketPort);
            System.out.println("Socket started");
        }, executorService);

        CompletableFuture.allOf(fileCollectionTask, socketTask).join();

        executorService.shutdown();

        TransferFiles();
    }
}
