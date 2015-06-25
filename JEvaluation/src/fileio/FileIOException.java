package fileio;


public class FileIOException extends java.io.IOException {

	public FileIOException() {
		super();
	}
	
	public FileIOException(String message) {
		super(message);
	}
	
	public FileIOException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FileIOException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 1L;

}
