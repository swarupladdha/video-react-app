package com.flexical.model;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.flexical.dao.SlotTimeIdValidator;

@Documented
@Constraint(validatedBy = SlotTimeIdValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSlotTimeId {

	String message() default "Invalid slotTimeId";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
