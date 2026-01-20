package com.solveda.catalogueservice.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String title) {
        super("Category with title '" + title + "' already exists");
    }
}
