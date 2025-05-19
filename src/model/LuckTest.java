package model;

public class LuckTest {
    private LuckTestOutcome success;
    private LuckTestOutcome failure;

    public LuckTest(LuckTestOutcome success, LuckTestOutcome failure) {
        this.success = success;
        this.failure = failure;
    }

    public LuckTestOutcome getSuccess() { return success; }
    public LuckTestOutcome getFailure() { return failure; }
}