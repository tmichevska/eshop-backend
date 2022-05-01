package com.management.project.eshopbackend.service.impl;

import com.management.project.eshopbackend.models.enumerations.OrderStatus;
import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.exceptions.NotEnoughQuantityException;
import com.management.project.eshopbackend.models.orders.DTO.OrderDto;
import com.management.project.eshopbackend.models.orders.DTO.ResponseOrderDTO;
import com.management.project.eshopbackend.models.orders.DTO.UpdateOrderStatusDTO;
import com.management.project.eshopbackend.models.orders.Order;
import com.management.project.eshopbackend.models.orders.ProductInOrder;
import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.models.shopping_cart.ProductInShoppingCart;
import com.management.project.eshopbackend.models.shopping_cart.ShoppingCart;
import com.management.project.eshopbackend.models.users.Postman;
import com.management.project.eshopbackend.models.users.User;
import com.management.project.eshopbackend.repository.*;
import com.management.project.eshopbackend.service.intef.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final UserJPARepository userJPARepository;
    private final OrderJPARepository orderJPARepository;
    private final ProductInOrderJPARepository productInOrderJPARepository;
    private final ShoppingCartJPARepository shoppingCartJPARepository;
    private final ProductInShoppingCartJPARepository productInShoppingCartJPARepository;
    private final PostmanJPARepository postmanJPARepository;
    private final ProductJPARepository productJPARepository;

    @Override
    public List<Order> getOrders(String username) {

        User user = userJPARepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return this.orderJPARepository.findAllByUser(user);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderJPARepository.findAll();
    }

    @Override
    public List<Order> getOrdersByPostman(String postmanUsername) {
        User postman = userJPARepository.findUserByUsername(postmanUsername)
                .orElseThrow(() -> new UsernameNotFoundException(postmanUsername));
        return this.orderJPARepository.findAllByPostman(postman);
    }


    @Override
    public Order makeOrder(OrderDto orderDto) {
        User user = userJPARepository.findUserByUsername(orderDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(orderDto.getUsername()));
        ShoppingCart shoppingCart = shoppingCartJPARepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No products in shopping cart!"));

        for (ProductInShoppingCart productInShoppingCart : shoppingCart.getProductsInShoppingCart()) {
            Product product = productJPARepository.getById(productInShoppingCart.getProduct().getId());
            int currentProductQuantity = product.getQuantity();
            if(currentProductQuantity - productInShoppingCart.getQuantity() < 0){
                throw new NotEnoughQuantityException("One or more products are not available.", product.getId());
            }
        }
        for (ProductInShoppingCart productInShoppingCart : shoppingCart.getProductsInShoppingCart()) {
            Product product = productJPARepository.getById(productInShoppingCart.getProduct().getId());
            int currentProductQuantity = product.getQuantity();
            product.setQuantity(currentProductQuantity - productInShoppingCart.getQuantity());
            productJPARepository.save(product);
            checkAndUpdateOtherCarts(shoppingCart, product); //namali kvantitet ili izbrishi proizvodi vo tugji
            //shopping carts ako vo magacin ima pomalku od tekovnite kvantiteti vo tugjite shopping carts
        }
        Order order = createOrder(orderDto, user);
        order.setOrderStatus(OrderStatus.CREATED);
        orderJPARepository.save(order);
        order.setProductsInOrder(addProductsToOrder(order, shoppingCart));
        orderJPARepository.save(order);
        return order;
    }

    @Override
    public ResponseOrderDTO convertToDto(Order order) {
        return Order.convertToDto(order);
    }

    @Override
    public Order changeOrderStatus(UpdateOrderStatusDTO updateOrderStatusDTO) {
        Order order = orderJPARepository.findById(updateOrderStatusDTO.getOrderId()).orElseThrow(() ->
                new EntityNotFoundException("No order with id " + updateOrderStatusDTO.getOrderId()));
        Postman postman = postmanJPARepository.findPostmenByUser_Id(order.getPostman().getId());
        if (updateOrderStatusDTO.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            postman.decreaseOrderCount();
            postmanJPARepository.save(postman);
        }
        order.setOrderStatus(updateOrderStatusDTO.getOrderStatus());
        orderJPARepository.save(order);
        return order;
    }

    private Order createOrder(OrderDto orderDto, User user) {

        List<Postman> postmanList = this.postmanJPARepository.findAllByCity(orderDto.getCity())
                .orElseThrow(() -> new EntityNotFoundException("No postman in city " + orderDto.getCity()));
        Postman postman = postmanList.stream().min(Comparator.comparing(Postman::getOrdersToDeliver)).orElseThrow(
                () -> new EntityNotFoundException("Postman not found!")
        );
        postman.updateCount();
        this.postmanJPARepository.save(postman);
        return new Order(user, postman.getUser(), orderDto.getCity(), orderDto.getTelephone(), orderDto.getAddress());
    }

    private List<ProductInOrder> addProductsToOrder(Order order, ShoppingCart shoppingCart) {

        List<ProductInOrder> productsInOrder = new ArrayList<>();
        shoppingCart.getProductsInShoppingCart().forEach(p -> productsInOrder.add(ProductInOrder.builder()
                .order(order)
                .product(p.getProduct())
                .quantity(p.getQuantity())
                .dateCreated(LocalDateTime.now())
                .build()));
        this.productInOrderJPARepository.saveAll(productsInOrder);
        this.productInShoppingCartJPARepository.deleteAllByShoppingCart(shoppingCart);
        this.shoppingCartJPARepository.save(shoppingCart);
        return productsInOrder;
    }

    private void checkAndUpdateOtherCarts(ShoppingCart shoppingCart, Product product){
        shoppingCartJPARepository.findAll().forEach(sp -> {
            if(!sp.getId().equals(shoppingCart.getId())) {
                ProductInShoppingCart pisc = sp.getProductsInShoppingCart().stream().filter(p ->
                        p.getProduct().getId().equals(product.getId())
                ).findFirst().orElse(null);
                if (pisc != null && product.getQuantity() < pisc.getQuantity()) {
                    if (product.getQuantity() == 0) {
                        productInShoppingCartJPARepository.deleteAllByIdAndShoppingCart(pisc.getId(), sp);
                    } else {
                        pisc.setQuantity(product.getQuantity());
                        productInShoppingCartJPARepository.save(pisc);
                    }
                }
            }
        });
    }
}
