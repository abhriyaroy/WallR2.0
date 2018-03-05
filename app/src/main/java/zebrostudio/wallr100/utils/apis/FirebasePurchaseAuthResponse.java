package zebrostudio.wallr100.utils.apis;


public class FirebasePurchaseAuthResponse {
    private String status;
    private String message;
    private int error_code;

    public int getError_code() {
        return error_code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
