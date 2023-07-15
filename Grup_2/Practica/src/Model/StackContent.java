package Model;

public class StackContent {
    private String type;
    private String closingLabel;
    private String startingLabel;

    public StackContent(String type, String closingLabel, String startingLabel) {
        this.type = type;
        this.closingLabel = closingLabel;
        this.startingLabel = startingLabel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClosingLabel() {
        return closingLabel;
    }

    public void setClosingLabel(String closingLabel) {
        this.closingLabel = closingLabel;
    }

    public String getStartingLabel() {
        return startingLabel;
    }

    public void setStartingLabel(String startingLabel) {
        this.startingLabel = startingLabel;
    }
}
