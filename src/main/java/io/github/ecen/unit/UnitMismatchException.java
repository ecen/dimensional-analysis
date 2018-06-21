package io.github.ecen.unit;

/**
 * Represents an illegal conversion or arithmetic operation.
 */
public class UnitMismatchException extends Exception {

	public UnitMismatchException(String message){
		super(message);
	}
	
	public UnitMismatchException() {
		this("");
	}
}
