package org.joychou.omni.checkmark.validation.checkfield;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public abstract class IpAddressValidator implements ConstraintValidator<IpAddress, String> {

    private static final String IP_ADDRESS_PATTERN =
            "^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 如果允许null，或者可以结合@NotNull注解
        }

        Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(value);

        if (!matcher.matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid IP format: " + value)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
    public static Input testMethod(Input ipAddress) {
        return ipAddress;
    }
}
