package exception;

public class CardinalityNotRecognizedException extends RuntimeException{
    public CardinalityNotRecognizedException() {
    }

    public CardinalityNotRecognizedException(String message) {
        super(message);
    }

    public CardinalityNotRecognizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardinalityNotRecognizedException(Throwable cause) {
        super(cause);
    }

    public CardinalityNotRecognizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
