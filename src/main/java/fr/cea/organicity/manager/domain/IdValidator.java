package fr.cea.organicity.manager.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

@Component
public class IdValidator implements ConstraintValidator<ValidateName, String> {

	public void initialize(ValidateName itf) {
	}

	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}

		for (String token : value.split(":")) {
			for (String subtoken : token.split("-"))
				if (!subtoken.matches("[a-zA-Z0-9]{2,40}"))
					return false;
		}

		return true;
	}
}
