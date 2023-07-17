package validators;

import java.util.ArrayList;

public final class ValidatorResult {

	//var
	private boolean validationPassed;
	private ArrayList<String> message;
	
	//ctor
	public ValidatorResult(boolean validationPassed, ArrayList<String> message) {
		super();
		this.validationPassed = validationPassed;
		this.message = message;
	}

	//getter 
	public boolean isValidationPassed() {
		return validationPassed;
	}

	public ArrayList<String> getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "ValidatorResult [validationPassed=" + validationPassed + ", message=" + message + "]";
	}

	
	
	
	
}
