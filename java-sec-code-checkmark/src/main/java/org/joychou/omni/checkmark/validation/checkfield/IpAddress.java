package org.joychou.omni.checkmark.validation.checkfield;



import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IpAddressValidator.class)
@Documented
public @interface IpAddress {
    String message() default "Invalid IP address";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
