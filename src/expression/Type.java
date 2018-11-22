package expression;

public enum Type {
    CONJ("&"),
    DISJ("|"),
    IMPL("->"),
    NOT("!"),
    SUM("+"),
    MUL("*"),
    QUOTE("\'"),
    ZERO("0"),
    EQUALS("="),
    COMMA(","),
    LBRACKET("("),
    RBRACKET(")"),
    FORALL("@"),
    EXISTS("?"),
    VAR(""),
    PREDICATE("");

    private String text;

    Type(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static Type fromString(String text) {
        for (Type s : Type.values()) {
            if (s.text.equals(text)) {
                return s;
            }
        }
        return null;
    }
}