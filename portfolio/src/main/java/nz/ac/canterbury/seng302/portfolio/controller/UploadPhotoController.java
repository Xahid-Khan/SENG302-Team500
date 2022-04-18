package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.PhotoCompressionAndStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Controller
public class UploadPhotoController {

    @Autowired
    private PhotoCompressionAndStorageService compressionAndStorageService;

    @RequestMapping("/upload")
    public String index() {
        return "upload_user_photo";
    }

    @PostMapping(value = "/upload")
    public String UploadPhoto(Model model, @RequestParam("image") MultipartFile file) throws IOException {
//        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
//        if (! new File(uploadDirectory).exists()) new File(uploadDirectory).mkdir();
//        try {
//            Files.write(fileNameAndPath, file.getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        compressionAndStorageService.compressImage(file);

        model.addAttribute("msg", "Successfully uploaded file");
        return "redirect:/my_account";
    }
}
