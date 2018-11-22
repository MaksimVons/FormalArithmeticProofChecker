package checker;

public class CheckResult {
    public boolean result;
    public String error;

    public CheckResult(boolean result, String error) {
        this.result = result;
        this.error = error;
    }
}
