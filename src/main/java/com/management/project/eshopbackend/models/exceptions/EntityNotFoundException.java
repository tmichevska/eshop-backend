package com.management.project.eshopbackend.models.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String m){
        super(m);
    }
}