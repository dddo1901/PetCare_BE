package TechWiz.auths.models.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleBasedValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRoleBasedData {
    String message() default "Invalid data for the specified role";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
