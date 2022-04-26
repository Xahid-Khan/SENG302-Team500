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
public class UploadPhotoService {
    public static String UPLOADDIRECTORY = System.getProperty("user.dir") + "/uploads";
    private final float IMAGEQUALITY = 0.3f;
    private String fileName;
    private String fileExtension;
    private ImageWriter imageWriter = null;
    private ImageWriteParam imageWriteParam = null;
    private ByteArrayOutputStream byteArrayOutputStream = null;
    private BufferedImage cropedImage = null;
    private BufferedImage originalImage = null;
    private ImageOutputStream imageOutputStream = null;
    private Path fileNameAndPath;
    private String errorInfo;
    private boolean success = false;

    public String imageProccessing(MultipartFile file, String userFileName) throws NullPointerException {
        fileName = file.getOriginalFilename();
        fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        imageWriter = ImageIO.getImageWritersByFormatName(fileExtension).next();; //An image writer is initialised for that specific type of image (file ext.)
        imageWriteParam = imageWriter.getDefaultWriteParam(); //Set default parameters for writing.
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); //Setting compression mode to explicit
        imageWriteParam.setCompressionQuality(IMAGEQUALITY); //setting image quality
        byteArrayOutputStream = new ByteArrayOutputStream();
        imageOutputStream = new MemoryCacheImageOutputStream(byteArrayOutputStream);
        imageWriter.setOutput(imageOutputStream); //Setting up the target type/file when writing file
        try (InputStream inputStream = file.getInputStream()) {
            originalImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            String info = String.format("compressImage - bufferedImage (file %s)- IOException - message: %s ", fileName, e.getMessage());
        }
        cropCenterSquare();
        saveUserImage(userFileName);
        if (success) {
            return "successful";
        } else {
            return errorInfo;
        }
    }


    /**
     * This method crops the photo into square. size is determined by the smallest edge of the photo. if we have an image
     * of size 6X10, our new image will be 6X6 (square).
     */
    private void cropCenterSquare() {
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
    }

    /**
     * This method saves the compressed and cropped image into the uploads folder. This method will check if the folder
     * exists. if not it will create a new one. if the file name exists in the folder than it will override the old file.
     */
    private void saveUserImage(String userFileName) {
        IIOImage image = new IIOImage(cropedImage, null, null);
        try {
            imageWriter.write(null, image, imageWriteParam);
        } catch (IOException e) {
            errorInfo = String.format("compressImage - imageWriter (file %s)- IOException - message: %s ", fileName, e.getMessage());
        } finally {
            imageWriter.dispose();
        }

        fileNameAndPath = Paths.get(UPLOADDIRECTORY, userFileName +"."+ fileExtension);
        if (!new File(UPLOADDIRECTORY).exists()) new File(UPLOADDIRECTORY).mkdir();
        try {
            Files.write(fileNameAndPath, byteArrayOutputStream.toByteArray());
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
