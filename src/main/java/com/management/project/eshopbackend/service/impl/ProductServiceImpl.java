package com.management.project.eshopbackend.service.impl;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.exceptions.IllegalAttributeValueException;
import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.models.products.Attribute;
import com.management.project.eshopbackend.models.products.Category;
import com.management.project.eshopbackend.models.products.DTO.ProductDTO;
import com.management.project.eshopbackend.models.shopping_cart.ShoppingCart;
import com.management.project.eshopbackend.repository.CategoryJPARepository;
import com.management.project.eshopbackend.repository.ProductJPARepository;
import com.management.project.eshopbackend.repository.AttributeJPARepository;
import com.management.project.eshopbackend.service.intef.ShoppingCartService;
import com.management.project.eshopbackend.service.intef.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductJPARepository productRepository;
    private final CategoryJPARepository categoryRepository;
    private final AttributeJPARepository attributeRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found!"));
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findAllByCategoryId(Long id) {
        return productRepository.findAll().stream().filter(product -> product.getCategory().getId().equals(id)).collect(Collectors.toList());
    }

    @Override
    public List<Product> filterProducts(long categoryId, double priceFrom, double priceTo, Map<Long, String> attributeIdAndValues) {
        return productRepository.findAll().stream().filter(product -> {
            boolean priceInRange = product.getPriceInMKD() >= priceFrom && product.getPriceInMKD() <= priceTo;
            AtomicBoolean attributesInRange = new AtomicBoolean(true);
            if(attributeIdAndValues != null){
                List<Long> keys = new ArrayList<>(attributeIdAndValues.keySet());
                for (long id : keys) {
                    Attribute currentAttribute = attributeRepository
                            .findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Attribute with id " + id + " not found!"));
                    String productAttributeValue = product.getValueForProductAttribute().get(currentAttribute);
                    if (productAttributeValue == null) {
                        attributesInRange.set(false);
                        break;
                    } else if (currentAttribute.isNumeric()) {
                        double[] fromToValues = Arrays.stream(attributeIdAndValues.get(id).split("-")).mapToDouble(Double::parseDouble).toArray();
                        Arrays.sort(fromToValues);
                        if (
                                !(fromToValues[0] <= Double.parseDouble(productAttributeValue)
                                        && fromToValues[1] >= Double.parseDouble(productAttributeValue))
                        ) {
                            attributesInRange.set(false);
                            break;
                        }
                    } else {
                        attributesInRange.set(Arrays.stream(attributeIdAndValues.get(id).split("-"))
                                .anyMatch(productAttributeValue::equalsIgnoreCase));
                        if(!attributesInRange.get())
                            break;
                    }
                }
            }
            if(categoryId == -1)
                return priceInRange && attributesInRange.get();
            else
                return priceInRange && attributesInRange.get() && product.getCategory().getId().equals(categoryId);
        }).collect(Collectors.toList());
    }

    @Override
    public Product create(ProductDTO productDTO) {
        if(productDTO.getAttributeIdAndValueMap().values().stream().anyMatch(value -> value.contains("-"))){
            throw new IllegalArgumentException("Attribute must not have a dash \"-\" in its value!");
        }
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + productDTO.getCategoryId() + " not found!"));
        Product product = Product.builder()
                .category(category)
                .productTitle(productDTO.getProductTitle())
                .productDescriptionHTML(productDTO.getProductDescriptionHTML())
                .quantity(productDTO.getQuantity())
                .priceInMKD(productDTO.getPriceInMKD())
                .pathToMainProductIMG("none")
                .pathsToProductIMGs(new LinkedList<>())
                .valueForProductAttribute(convertAttributeIdMapToAttributeMap(productDTO.getAttributeIdAndValueMap()))
                .dateCreated(LocalDateTime.now())
                .build();
        return productRepository.save(product);
    }

    @Override
    public Product update(ProductDTO productDTO) {
        if(productDTO.getAttributeIdAndValueMap().values().stream().anyMatch(value -> value.contains("-"))){
            throw new IllegalArgumentException("Attribute must not have a dash \"-\" in its value!");
        }
        Product product = findById(productDTO.getId());
        product.setProductTitle(productDTO.getProductTitle());
        product.setProductDescriptionHTML(productDTO.getProductDescriptionHTML());
        product.setQuantity(productDTO.getQuantity());
        product.setPriceInMKD(productDTO.getPriceInMKD());

        if (!productDTO.getId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id " + productDTO.getCategoryId() + " not found!"));
            product.setCategory(category);
            product.setValueForProductAttribute(convertAttributeIdMapToAttributeMap(productDTO.getAttributeIdAndValueMap()));
        }
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

 @Override
    public void updateProductAttributesForCategoryId(Long categoryId){
        List<Product> allProductsUnderThisCategory = productRepository.findAll().stream()
                .filter(product -> product.getCategory().getId().equals(categoryId)).collect(Collectors.toList());
        if(allProductsUnderThisCategory.size()==0)
            return;
        List<Attribute> newAttributes = allProductsUnderThisCategory.get(0).getCategory().getAttributes();
        allProductsUnderThisCategory.forEach(product -> {
            HashMap<Attribute, String> newMap = new HashMap<>();
            newAttributes.forEach(attribute -> newMap.put(attribute, ""));
            product.getValueForProductAttribute().forEach((key, value) -> {
                if(newAttributes.contains(key))
                    newMap.put(key, value);
            });
            product.setValueForProductAttribute(newMap);
        });
        productRepository.saveAll(allProductsUnderThisCategory);
    }

    private HashMap<Attribute, String> convertAttributeIdMapToAttributeMap(Map<Long, String> attributeIdAndValueMap){
        HashMap<Attribute, String> attributeAndValues = new HashMap<>();
        attributeIdAndValueMap.forEach((key, value) -> attributeAndValues.put(attributeRepository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Attribute with id " + key + " not found!")), value));
        attributeAndValues.forEach((key, value) -> {
            if(key.isNumeric() && !isNumeric(value))
                throw new IllegalAttributeValueException(key.getName(), value);
        });
        return attributeAndValues;
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public void updateMainProductImage(Long id, String filePath) {
        Product product = findById(id);
        product.setPathToMainProductIMG(filePath);
        productRepository.save(product);
    }

    @Override
    public void updateAllProductImages(Long id, List<String> filePaths) {
        Product product = findById(id);
        product.setPathsToProductIMGs(filePaths);
        productRepository.save(product);
    }

    @Override
    public void deleteProductImage(Long id, Integer imageId) {
        Product product = findById(id);
        product.getPathsToProductIMGs().remove(imageId + ".jpg");
        if(product.getPathsToProductIMGs().size()==0)
            product.setPathToMainProductIMG("none");
        productRepository.save(product);
    }

    @Override
    public void addNewProductImage(Long id, String filename) {
        Product product = findById(id);
        product.getPathsToProductIMGs().add(filename);
        productRepository.save(product);
    }

    @Override
    public boolean checkProductQuantity(Long id, int quantity, String username) {
        ShoppingCart sp = shoppingCartService.getShoppingCart(username);
        if(
                sp.getProductsInShoppingCart().stream().filter(p -> p.getProduct().getId().equals(id)).findFirst().orElse(null) != null &&
                        sp.getProductsInShoppingCart().stream().filter(p -> p.getProduct().getId().equals(id)).findFirst().get().getQuantity()+quantity >
                                findById(id).getQuantity()
        )
            return false;
        return findById(id).getQuantity()>=quantity;
    }
}
