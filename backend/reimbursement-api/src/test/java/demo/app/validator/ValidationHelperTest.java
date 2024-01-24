package demo.app.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidationHelperTest {
    @Mock
    private Validator validator;
    @InjectMocks
    private ValidationHelper validationHelper;
    @Mock
    private ConstraintViolation<Object> constraintViolation;

    @Test
    void testValidateWithNoViolations() {
        Object request = new Object();
        Set<ConstraintViolation<Object>> constraintViolations = new HashSet<>();
        given(validator.validate(any())).willReturn(constraintViolations);
        validationHelper.validate(request);
        verify(validator, times(1)).validate(any());
    }

    @Test
    void testValidateWithViolations() {
        Object request = new Object();
        Set<ConstraintViolation<Object>> constraintViolations = new HashSet<>();
        constraintViolations.add(constraintViolation);
        given(validator.validate(any())).willReturn(constraintViolations);

        assertThrows(ConstraintViolationException.class, () -> validationHelper.validate(request));

        verify(validator, times(1)).validate(any());
    }
}