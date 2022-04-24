package com.management.project.eshopbackend.service.intef;

import com.management.project.eshopbackend.models.products.DTO.ProductDTO;
import com.management.project.eshopbackend.models.products.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Product findById(Long id);

    List<Product> findAll();

    List<Product> findAllByCategoryId(Long id);

    List<Product> filterProducts(long categoryId, double priceFrom, double priceTo, Map<Long, String> attributeIdAndValue);

    Product create(ProductDTO productDTO);

    Product update(ProductDTO productDTO);

    boolean checkProductQuantity(Long id, int quantity, String username);

    void delete(Long id);

    void updateProductAttributesForCategoryId(Long categoryId);

    void updateMainProductImage(Long id, String filePath);

    void updateAllProductImages(Long id, List<String> filePaths);

    void deleteProductImage(Long id, Integer imageId);

    void addNewProductImage(Long id, String filename);
}
