package com.management.project.eshopbackend.service.intef;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {
    public void init();

    public int saveNewImage(MultipartFile file, Long productId);

    public Resource load(String filename);

    public List<String> getNumberOfImagesForProductId(Long productId);
}
