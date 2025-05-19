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

class LuckTestOutcome {
    private String text;
    private int enduranceModifier;

    public LuckTestOutcome(String text, int enduranceModifier) {
        this.text = text;
        this.enduranceModifier = enduranceModifier;
    }

    public String getText() { return text; }
    public int getEnduranceModifier() { return enduranceModifier; }
}