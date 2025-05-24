package org.joychou.omni.checkmark.validation.checktype;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public abstract class TypeInputValidator implements ConstraintValidator<TypeInputAnnotation, TypeInput> {

    public boolean isValid(TypeInput typeInput, ConstraintValidatorContext context) {
        String input = typeInput.getInput();
        if (input == null || input.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(input)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}