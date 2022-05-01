package com.management.project.eshopbackend.web.rest;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.products.DTO.CheckQuantityDTO;
import com.management.project.eshopbackend.models.products.DTO.ProductDTO;
import com.management.project.eshopbackend.models.products.DTO.ProductEnoughQuantityDTO;
import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.service.intef.ImageStorageService;
import com.management.project.eshopbackend.service.intef.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
                        cqDTO.getProductId(), cqDTO.getQuantity(), authentication.getName()
                ), Product.convertToDTO(productService.findById(cqDTO.getProductId())));
        return new ResponseEntity<>(peqDTO, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(@RequestParam MultiValueMap<String, String> filters) {
        //vo filters mora konstanto da se zapazat ovie fakti:
        //key -> value (shto oznachuvaat primerite podole

        //pricerange -> valuta:0-100 (primer, ova vazhi za site proizvodi bidejki site imaat cena)
        //attributes -> key:value (odeleni so zapirka, bidejki imame razlichni atributi, kje idat vo eden key
        //site, a potoa vo lista od key:value), taka shto eden povik primer za atribut za tezhina i vid so id 1 i 2
        //bi bil: attributes=1:5-100,2:crveno
        //so ova, filtrirame atribut so id 1 (tezhina) od 5-100 kg, i atribut so id 2 (vid) da e ednakov na 'crveno'

        //celosen povik bi bilo: /api/products/filter?categoryid=2&pricerange=mkd:0-100&attributes=1:5-100,2:crveno
        String priceRange = filters.get("pricerange").get(0);
        String attributes = filters.get("attributes").get(0);
        long categoryId = Long.parseLong(filters.get("categoryid").get(0));
        String[] priceParts = priceRange.split(":"); //priceParts[0] e valutata, priceParts[1] e range-ot
        //sega za sega valutata e mkd

        double[] fromToValues = Arrays.stream(priceParts[1].split("-")).mapToDouble(Double::parseDouble).toArray();
        Arrays.sort(fromToValues);
        if (attributes.isBlank() || attributes.isEmpty()) {
            List<ProductDTO> products = productService
                    .filterProducts(categoryId, fromToValues[0], fromToValues[1], null)
                    .stream().map(Product::convertToDTO).collect(Collectors.toList());
            return new ResponseEntity<>(products, HttpStatus.OK);
        } else {
            Map<Long, String> attributeIdAndValueMap = Arrays.stream(attributes.split(","))
                    .map(string -> string.split(":"))
                    .collect(Collectors.toMap(strings -> Long.parseLong(strings[0]), strings -> strings[1]));
            List<ProductDTO> products = productService
                    .filterProducts(categoryId, fromToValues[0], fromToValues[1], attributeIdAndValueMap)
                    .stream().map(Product::convertToDTO).collect(Collectors.toList());
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return new ResponseEntity<>("Product with id " + id + " deleted.", HttpStatus.OK);
    }

    @PutMapping("/img/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    @PutMapping("/img")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setMainProductImage(@RequestParam Long productId, @RequestParam Integer mainImageId) {
        productService.updateMainProductImage(productId, mainImageId + ".jpg");
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/img")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addAllProductImages(@RequestParam Long productId, @RequestParam Integer mainImageId,
                                                 @RequestParam MultipartFile[] images) {
        String message = "";
        LinkedList<String> fileNames = new LinkedList<>();
        try {
            Arrays.stream(images).forEach(image -> {
                int imgNum = imageStorageService.saveNewImage(image, productId);
                fileNames.add(imgNum + ".jpg");
            });
            message = "Images saved successfully!";
            productService.updateAllProductImages(productId, fileNames);
            productService.updateMainProductImage(productId, fileNames.get(mainImageId - 1));
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Failed to save images!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @DeleteMapping("/img/delete/{productId}/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProductImage(@PathVariable Long productId, @PathVariable Integer imageId) {
        productService.deleteProductImage(productId, imageId);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @GetMapping("/images/{productId}")
    public ResponseEntity<List<String>> getImagePathsForProductId(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(imageStorageService.getNumberOfImagesForProductId(productId));
    }

    @GetMapping("/images/main/{productId}")
    public ResponseEntity<String> getMainImagePathForProductId(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findById(productId).getPathToMainProductIMG());
    }

    @GetMapping("/images/{productId}/{resolution}/main")
    public ResponseEntity<Resource> getMainImageForProductId(@PathVariable Long productId,
                                                             @PathVariable String resolution) {
        String filename = productService.findById(productId).getPathToMainProductIMG();
        Resource file = null;
        if (!filename.equals("none"))
            file = imageStorageService.load(productId + File.separator + resolution + File.separator + filename);
        else {
            file = imageStorageService.load("placeholder.png");
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/images/{productId}/{resolution}/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String productId, @PathVariable String resolution,
                                             @PathVariable String imageName) {
        Resource file = imageStorageService
                .load(productId + File.separator + resolution + File.separator + imageName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
