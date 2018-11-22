package expression.parser;

import expression.Type;

import static expression.Type.PREDICATE;
import static expression.Type.VAR;

public class Token {
    public Type type;

    private String text;

    public Token(Type type) {
        this.type = type;
    }

    public Token(String str) {
        if (str.toLowerCase().equals(str)) {
            type = VAR;
            this.text = str;
        } else {
            type = PREDICATE;
            this.text = str;
        }
    }

    public String getText() {
        if (type == VAR || type == PREDICATE) return text;
        return type.getText();
    }
}
