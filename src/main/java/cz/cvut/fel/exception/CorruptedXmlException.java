package cz.cvut.fel.exception;

/**
 * Exception encountered during xml file reading
 */
public class CorruptedXmlException extends RuntimeException{
    public CorruptedXmlException() {
    }

    public CorruptedXmlException(String message) {
        super(message);
    }

    public CorruptedXmlException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorruptedXmlException(Throwable cause) {
        super(cause);
    }

    public CorruptedXmlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
