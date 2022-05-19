package nz.ac.canterbury.seng302.portfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;


/**
 * A service that provides utilities to crop and compress uploaded images for storage.
 */
@Service
public class PhotoCropService {
    public static final float DEFAULT_IMAGE_OUTPUT_QUALITY = 0.3f;

    /**
     * Helper method that returns an ImageWriter for the target output Mime-Type.
     *
     * @param mimeType to output
     * @return ImageWriter for that mime-type
     * @throws UnsupportedMediaTypeStatusException when no ImageWriter exists for the given mimeType
     */
    public ImageWriter getImageWriterForMimeType(String mimeType) throws UnsupportedMediaTypeStatusException {
        var imageWriterIterator = ImageIO.getImageWritersByMIMEType(mimeType);

        try {
            return imageWriterIterator.next();
        }
        catch (NoSuchElementException e) {
            throw new UnsupportedMediaTypeStatusException("This media type is unsupported.");
        }
    }

    /**
     * Crops the given image into a square if necessary.
     *
     * @param unCroppedImage to crop
     * @return The same image, cropped to the centre so that it is square
     */
    public BufferedImage cropCenterSquare(BufferedImage unCroppedImage) {
        int height = unCroppedImage.getHeight();
        int width = unCroppedImage.getWidth();

        if (height != width) {
            int squareSize = Math.min(height, width);
            int xCoordinates = width / 2;
            int yCoordinates = height / 2;

            return unCroppedImage.getSubimage(
                    xCoordinates - (squareSize/2),
                    yCoordinates - (squareSize/2),
                    squareSize,
                    squareSize
            );
        }

        return unCroppedImage;
    }

    /**
     * Compresses and saves an image into a byte array. The image data is in the format specified by the outputMimeType.
     *
     * @param imageData to compress and then save
     * @param outputMimeType describing the image format to use
     * @param quality of the compressed image output
     * @return Byte array of the data in the image
     * @throws UnsupportedMediaTypeStatusException when an illegal outputMimeType is specified
     */
    public byte[] compressAndSaveImageToByteArray(RenderedImage imageData, String outputMimeType, float quality) throws UnsupportedMediaTypeStatusException, IOException {
        var image = new IIOImage(imageData, null, null);
        var imageWriter = getImageWriterForMimeType(outputMimeType);

        // Configure compression options
        var writerParams = imageWriter.getDefaultWriteParam();
        writerParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writerParams.setCompressionQuality(quality);

        // Set up buffer for recording the output
        var buffer = new ByteArrayOutputStream();
        var bufferIntermediary = new MemoryCacheImageOutputStream(buffer);
        imageWriter.setOutput(bufferIntermediary);

        try {
            imageWriter.write(null, image, writerParams);
        }
        finally {
            imageWriter.dispose();
        }

        return buffer.toByteArray();
    }

    /**
     * Compresses and saves an image into a byte array. The image data is in the format specified by the outputMimeType.
     * The {@link PhotoCropService#DEFAULT_IMAGE_OUTPUT_QUALITY} compression quality is used.
     *
     * @param imageData to compress and then save
     * @param outputMimeType describing the image format to use
     * @return Byte array of the data in the image
     * @throws UnsupportedMediaTypeStatusException when an illegal outputMimeType is specified
     */

    public byte[] compressAndSaveImageToByteArray(RenderedImage imageData, String outputMimeType) throws UnsupportedMediaTypeStatusException, IOException {
        return compressAndSaveImageToByteArray(imageData, outputMimeType, DEFAULT_IMAGE_OUTPUT_QUALITY);
    }

    /**
     * Processes an image from a stream through the following steps:
     *
     * <ol>
     *     <li>Read from the given <code>imageData</code></li>
     *     <li>Crop around the centre so that it is square. See {@link PhotoCropService#cropCenterSquare(BufferedImage)}</li>
     *     <li>Compress the image and save it using the given <code>outputMimeType</code></li>
     * </ol>
     *
     * @param imageData for the image to process
     * @param outputMimeType describing the image format to use
     * @return Byte array of the data in the image
     * @throws UnsupportedMediaTypeStatusException when an illegal outputMimeType is specified
     */
    public byte[] processImage(InputStream imageData, String outputMimeType) throws UnsupportedMediaTypeStatusException, IOException {
        var image = ImageIO.read(imageData);

        var croppedImage = cropCenterSquare(image);

        return compressAndSaveImageToByteArray(croppedImage, outputMimeType);
    }

    /**
     * Processes an image from a Multi-Part File through the following steps:
     *
     * <ol>
     *     <li>Read from the given <code>imageData</code></li>
     *     <li>Crop around the centre so that it is square. See {@link PhotoCropService#cropCenterSquare(BufferedImage)}</li>
     *     <li>Compress the image and save it using the Mime-Type specified in the file data</li>
     * </ol>
     *
     * @param file containing the image data and mime-type
     * @return Byte array of the data in the image
     * @throws UnsupportedMediaTypeStatusException when an illegal outputMimeType is specified
     */
    public byte[] processImageFile(MultipartFile file) throws UnsupportedMediaTypeStatusException, IOException {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new UnsupportedMediaTypeStatusException("A media type is required.");
        }

        return processImage(file.getInputStream(), contentType);
    }
}
