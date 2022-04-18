package nz.ac.canterbury.seng302.portfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PhotoCompressionAndStorageService {
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    public byte[] compressImage(MultipartFile mpFile) {
        float quality = 0.3f;
        String imageName = mpFile.getOriginalFilename();
        String imageExtension = imageName.substring(imageName.lastIndexOf(".") + 1);

        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(imageExtension).next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // Check the api value that suites your needs.

        imageWriteParam.setCompressionQuality(quality);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(baos);

        imageWriter.setOutput(imageOutputStream);
        BufferedImage originalImage = null;

        try (InputStream inputStream = mpFile.getInputStream()) {
            originalImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            String info = String.format("compressImage - bufferedImage (file %s)- IOException - message: %s ", imageName, e.getMessage());
            return baos.toByteArray();
        }

        BufferedImage cropedImage = null;

        // Resizing

        int height = originalImage.getHeight();
        int width = originalImage.getWidth();

        if (height != width) {
            int squaresize = height > width ? width : height;
            int xCoordinates = width / 2;
            int yCoordinates = height / 2;

            cropedImage = originalImage.getSubimage(
                    xCoordinates - (squaresize/2),
                    yCoordinates - (squaresize/2),
                    squaresize,
                    squaresize
            );
        }



        IIOImage image = new IIOImage(cropedImage, null, null);
        try {
            imageWriter.write(null, image, imageWriteParam);
        } catch (IOException e) {
            String info = String.format("compressImage - imageWriter (file %s)- IOException - message: %s ", imageName, e.getMessage());
        } finally {
            imageWriter.dispose();
        }

        // ------------------------------
        Path fileNameAndPath = Paths.get(uploadDirectory, mpFile.getOriginalFilename());
        if (!new File(uploadDirectory).exists()) new File(uploadDirectory).mkdir();
        try {
            Files.write(fileNameAndPath, baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  ------------------------------

        return baos.toByteArray();
    }
}
