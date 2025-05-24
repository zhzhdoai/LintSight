package org.joychou.omni.checkmark.validation.checktype;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TypeInputValidator.class)
@Documented
public @interface TypeInputAnnotation {
    String message() default "Invalid Input";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}

