package com.example.laboratorjavafx.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
