package com.management.project.eshopbackend.service.impl;

import com.management.project.eshopbackend.service.intef.ImageStorageService;
import com.management.project.eshopbackend.service.intef.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageStorageServiceImpl implements ImageStorageService {
    private final ProductService productService;
    private final Path root = Paths.get("images");
    private final Map<String, Integer> imageResolutions = new HashMap<>() {{
        //size, maxHeight
        put("s", 150); //small, 150px height
        put("m", 500); //medium, 500px height
        put("l", 1000); //large, 1000px height
        put("o", null); //original
    }};

    @Override
    public void init() {
        try {
            if(!Files.exists(root))
                Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public int saveNewImage(MultipartFile file, Long productId) {
        File dummyForNumber = new File(String.valueOf(this.root.resolve(productId.toString())));
        if(!Files.exists(dummyForNumber.toPath())){
            try {
                Files.createDirectory(dummyForNumber.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(dummyForNumber.listFiles().length != 0)
            dummyForNumber = new File(String.valueOf(this.root.resolve(productId + File.separator + imageResolutions.keySet().stream().findAny().get() + File.separator)));
        int nextImageNumber = dummyForNumber.listFiles().length + 1;
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            imageResolutions.forEach((size, height) -> {
                double aspectRatio = (double) image.getWidth() / image.getHeight();
                BufferedImage finalImage = null;
                if(height == null || height>image.getHeight(null)){
                    finalImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    finalImage.getGraphics().drawImage(image, 0, 0, Color.WHITE, null);
                }
                else{
                    Image temp = image.getScaledInstance((int) (height * aspectRatio), height, Image.SCALE_SMOOTH);
                    finalImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    finalImage.getGraphics().drawImage(temp, 0, 0, Color.WHITE, null);
                }
                try {
                    File outputFile = new File(String.valueOf(this.root.resolve(productId + File.separator + size + File.separator + nextImageNumber + ".jpg")));
                    outputFile.getParentFile().mkdirs();
                    ImageIO.write(finalImage,"jpg", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
        return nextImageNumber;
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public List<String> getNumberOfImagesForProductId(Long productId) {
        return productService.findById(productId).getPathsToProductIMGs();
    }
}
