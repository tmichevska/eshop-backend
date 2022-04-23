package com.management.project.eshopbackend.models.exceptions;

import lombok.Getter;

@Getter
public class NotEnoughQuantityException extends RuntimeException{
    Long productId;
    public NotEnoughQuantityException(String m, Long productId){
        super(m);
        this.productId = productId;
    }
}