package com.management.project.eshopbackend.web.rest;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.products.DTO.CheckQuantityDTO;
import com.management.project.eshopbackend.models.products.DTO.ProductDTO;
import com.management.project.eshopbackend.models.products.DTO.ProductEnoughQuantityDTO;
import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.service.intef.ImageStorageService;
import com.management.project.eshopbackend.service.intef.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@CrossOrigin(value = "*")
public class ProductController {
    private final ProductService productService;
    private final ImageStorageService imageStorageService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Product product;
        try {
            product = productService.findById(id);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(Product.convertToDTO(product), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<ProductDTO> products = productService.findAll().stream().map(Product::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewProduct(@RequestBody ProductDTO productDTO) {
        Product product;
        try {
            product = productService.create(productDTO);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.toString(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Product.convertToDTO(product), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductDTO productDTO) {
        Product product;
        try {
            product = productService.update(productDTO);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.toString(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Product.convertToDTO(product), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return new ResponseEntity<>("Product with id " + id + " deleted.", HttpStatus.OK);
    }

    @GetMapping("/bycat/{id}")
    public ResponseEntity<?> getAllProductsByCategoryId(@PathVariable Long id) {
        List<ProductDTO> products = productService.findAllByCategoryId(id).stream().map(Product::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/check-quantity")
    public ResponseEntity<?> checkProductQuantity(@RequestBody CheckQuantityDTO cqDTO, Authentication authentication) {
        if (authentication == null) {
            return new ResponseEntity<>("Unauthenticated", HttpStatus.OK);
        }
        ProductEnoughQuantityDTO peqDTO = new ProductEnoughQuantityDTO(
                productService.checkProductQuantity(
                        cqDTO.getProductId(), cqDTO.getQuantity(), authentication.getName()),
                Product.convertToDTO(productService.findById(cqDTO.getProductId())));
        return new ResponseEntity<>(peqDTO, HttpStatus.OK);
    }

    @PutMapping("/img/{id}")
    public ResponseEntity<?> addNewProductImage(@PathVariable Long id, MultipartFile image) {
        String message = "";
        String fileName = "";
        try {
            fileName = imageStorageService.saveNewImage(image, id) + ".jpg";
            message = "Image saved successfully!";
            productService.addNewProductImage(id, fileName);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Failed to save image!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }

    }
}
