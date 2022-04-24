package com.management.project.eshopbackend.web.rest;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.products.DTO.ProductDTO;
import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.service.intef.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@CrossOrigin(value = "*")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id){
        Product product;
        try {
            product = productService.findById(id);
        }
        catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(Product.convertToDTO(product), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(){
        List<ProductDTO> products = productService.findAll().stream().map(Product::convertToDTO).collect(Collectors.toList());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

}
