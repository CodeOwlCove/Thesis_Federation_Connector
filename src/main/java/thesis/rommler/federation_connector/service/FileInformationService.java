package thesis.rommler.federation_connector.service;

import org.springframework.stereotype.Service;
import thesis.rommler.federation_connector.api.answerClasses.FileInformation;
import thesis.rommler.federation_connector.api.controller.FileInformationController;
import thesis.rommler.federation_connector.service.FileTransferService.FileTransferService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class is responsible for collecting information about the files in the Assets folder.
 */
@Service
public class FileInformationService extends FileTransferService{

    private Logger logger = Logger.getLogger(FileInformationService.class.getName());

    public FileInformationService(ConnectionService connectionService) {
        super(connectionService);
    }

    /**
     * Collects all files from the Assets folder and returns them as a list of FileInformation objects.
     * @return list of FileInformation objects
     */
    public ArrayList<FileInformation> CollectFileInformation(){
        ArrayList<FileInformation> fileInformation = new ArrayList<FileInformation>();

        List<File> files = getFiles(assetFolderPath);

        logger.info("Collecting file information from: " + assetFolderPath + "...");

        for (File file : files) {
            fileInformation.add(new FileInformation(file.getName(), getExtension(file.getName()), String.format("%,d", file.length())));
        }

        return fileInformation;
    }

    /**
     * Get all files from a directory
     *
     * @param directoryPath folder path
     * @return list of files
     */
    private static List<File> getFiles(String directoryPath) {
        List<File> files = new ArrayList<>();
        File directory = new File(directoryPath);
        File[] directoryContents = directory.listFiles();
        if (directoryContents != null) {
            for (File file : directoryContents) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Get the extension of a file
     *
     * @param filename file name
     * @return extension of the file
     */
    public static String getExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return "";
        }
        return filename.substring(lastIndexOfDot + 1);
    }
}
