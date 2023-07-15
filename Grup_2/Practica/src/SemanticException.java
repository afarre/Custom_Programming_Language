public class SemanticException extends Exception {
    public String message;
    private String eType;
    private String eLexema;
    public static final int VAR_NOT_DECLARED = 0;
    public static final int VAR_DECLARED = 1;
    public static final int INCOMPAT_TYPES = 2;

    public SemanticException(String varType, String lexema, int type){
        this.eType = varType;
        this.eLexema = lexema;

        switch (type) {
            case 0:
                this.message = "Variable " + this.eLexema + " is not declared.";
                break;
            case 1:
                this.message = "Variable " + this.eLexema + " is already declared.";
                break;
            case 2:
                this.message = "Variable " + this.eLexema + " is of type " + this.eType + ". You have to assign another " + this.eType + ".";
                break;
            default:
                this.message = "Unknown error in " + this.eLexema;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }
}
