package com.systems.integrated.eshopshopbackend.models.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String m){
        super(m);
    }
}