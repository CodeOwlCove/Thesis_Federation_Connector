package thesis.rommler.federation_connector.api.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thesis.rommler.federation_connector.api.answerClasses.FileInformation;
import thesis.rommler.federation_connector.api.answerClasses.GetFilesAnswer;
import thesis.rommler.federation_connector.service.FileInformationService;
import thesis.rommler.federation_connector.service.LoginService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class FileInformationController {

    private Logger logger = Logger.getLogger(FileInformationController.class.getName());

    private final FileInformationService fileInformationService;

    public FileInformationController(FileInformationService fileInformationService){
        this.fileInformationService = fileInformationService;
    }


    @GetMapping("/GetFileInformation")
    public ResponseEntity<ArrayList<FileInformation>> getFileInformation() {
        logger.info("GetFileInformation request received");
        return ResponseEntity.ok().body(fileInformationService.CollectFileInformation());
    }

    @GetMapping("/CheckIfHasFile")
    public boolean CheckIfHasFile(@RequestParam String[] fileNames) {
        return fileInformationService.CheckForFiles(fileNames);
    }

}
