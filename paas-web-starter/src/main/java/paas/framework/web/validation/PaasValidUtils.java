
package paas.framework.web.validation;


import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import paas.framework.tools.JSON;

import java.util.Set;

public class PaasValidUtils {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> ValidationResult validate(T object, Class<?>... groups) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        for (ConstraintViolation<T> violation : violations) {
            result.addError(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return result;
    }

    public static <T> void validateException(T object, Class<?>... groups) {
        ValidationResult result = validate(object, groups);
        boolean valid = result.isValid();
        if (!valid) {
            BusException.fail(ResultMessage.PARAMETER_ERROR.getCode(), JSON.toJSONString(result.getErrors()));
        }
    }
}
