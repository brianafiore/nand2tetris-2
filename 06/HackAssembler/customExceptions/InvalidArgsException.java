package customExceptions;

public class InvalidArgsException extends RuntimeException {
	public InvalidArgsException(String msg){
		super(msg);
//		System.out.println(msg);
	}
}
