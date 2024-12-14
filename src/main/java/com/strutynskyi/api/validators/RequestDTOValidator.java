package com.strutynskyi.api.validators;

import com.strutynskyi.api.exceptions.ObjectNotValidException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class RequestDTOValidator<T> {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public void validate(T object) {
        var violations = validator.validate(object);
        if (!violations.isEmpty()) {
            var errorMessages = violations
                    .stream()
                    .sorted(Comparator.comparing(ConstraintViolation::getMessage))
                    .map(ConstraintViolation::getMessage)
                    .toList();
            throw new ObjectNotValidException(errorMessages);
        }
    }
}
