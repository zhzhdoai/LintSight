package org.joychou.omni.checkmark.validation;

import org.joychou.omni.checkmark.validation.checkfield.Input;
import org.joychou.omni.checkmark.validation.checktype.TypeInput;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

import static org.joychou.omni.checkmark.validation.checkfield.IpAddressValidator.testMethod;

@RestController
@RequestMapping("/api")
public class HibernateValidateController {

    @PostMapping("/validateBody")
    public ResponseEntity<?> validateBody(@Valid @RequestBody Input input, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation failed", errorMessage));
        }
        testMethod(input);
        return ResponseEntity.ok("valid");
    }
    @PostMapping("/validateType")
    public ResponseEntity<?> validateType(@Valid @RequestBody TypeInput input, BindingResult result) {
        return ResponseEntity.ok("valid");
    }

    // 简单的错误响应类
    private static class ErrorResponse {
        private String message;
        private String details;

        public ErrorResponse(String message, String details) {
            this.message = message;
            this.details = details;
        }

        // Getters
        public String getMessage() { return message; }
        public String getDetails() { return details; }
    }
}

