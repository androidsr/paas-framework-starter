package paas.framework.web.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private List<ValidationError> errors = new ArrayList<>();

    public void addError(String field, String message) {
        errors.add(new ValidationError(field, message));
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
}
