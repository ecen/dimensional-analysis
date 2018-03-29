public class UnitMismatchException extends Exception {

	public UnitMismatchException(String message){
		super(message);
	}
	
	public UnitMismatchException() {
		this("");
	}
}
