package expression.parser;

import expression.*;

import java.util.ArrayList;
import java.util.List;

import static expression.Type.*;

public class Parser {
    private int parseInd = 0;
    private int ind = 0;
    private List<Token> tokens;

    public Expression parse(String line, boolean reset) {
        if (tokens == null || reset) {
            String str = line.replaceAll("\\s+", "");
            ///str = "(" + str + ")";
            parseInd = 0;
            ind = 0;
            tokens = getTokensFromLine(str);
        } else {
            ind++; // for comma in assumptions
            if (tokens.size() <= ind)
                return null;
        }
        if (tokens.isEmpty()) return null;
        return getImpl();
    }

    private Expression getImpl() {
        Expression expr = getDisj();
        while (ind < tokens.size() && tokens.get(ind).type == IMPL) {
            ind++;
            expr = new Binary(expr, getImpl(), IMPL);
        }
        return expr;
    }

    private Expression getDisj() {
        Expression expr = getConj();
        while (ind < tokens.size() && tokens.get(ind).type == DISJ) {
            ind++;
            expr = new Binary(expr, getConj(), DISJ);
        }
        return expr;
    }

    private Expression getConj() {
        Expression expr = getUnary();
        while (ind < tokens.size() && tokens.get(ind).type == CONJ) {
            ind++;
            expr = new Binary(expr, getConj(), CONJ);
        }
        return expr;
    }

    private Expression getUnary() {
        switch (tokens.get(ind++).type) {
            case PREDICATE: {
                ind--;
                return getPrecidate();
            }
            case FORALL: {
                Expression var = new Var(tokens.get(ind++).getText());
                Expression unary = getUnary();
                return new Binary(var, unary, FORALL);
            }
            case EXISTS: {
                Expression var = new Var(tokens.get(ind++).getText());
                Expression unary = getUnary();
                return new Binary(var, unary, EXISTS);
            }
            case LBRACKET: {
                ind--;
                // for difference between (impl) and (term)=... :
                if (isNextEquality()) {
                    Expression expr = getPrecidate();
                    return expr;
                } else {
                    ind++;
                    Expression expr = getImpl();
                    ind++;
                    return expr;
                }
            }
            case NOT:
                return new Negate(getUnary());
            default: // var
                ind--;
                return getPrecidate();
        }
    }

    private boolean isNextEquality() {
        int equalPosition = ind;
        while (equalPosition < tokens.size() && tokens.get(equalPosition).type != EQUALS) {
            equalPosition++;
        }
        if (equalPosition == tokens.size())
            return false;
        int bracketsBalance = 0;
        int curInd = ind;
        while (curInd != equalPosition) {
            if (tokens.get(curInd).type == LBRACKET) {
                bracketsBalance++;
            } else if (tokens.get(curInd).type == RBRACKET) {
                bracketsBalance--;
            }
            if (bracketsBalance < 0)
                return false;
            curInd++;
        }
        return bracketsBalance == 0;
    }

    private Expression getPrecidate() {
        if (tokens.get(ind).type == PREDICATE) {
            String name = tokens.get(ind).getText();
            ArrayList<Expression> terms = new ArrayList<>();
            ind++;
            if (ind < tokens.size() && tokens.get(ind).type == LBRACKET) {
                ind++;
                terms.add(getTerm());
                while (tokens.get(ind).type == COMMA) {
                    ind++;
                    terms.add(getTerm());
                }
                ind++;
            }
            return new Predicate(name, terms);
        }
        Expression l = getTerm();
        ind++;
        Expression r = getTerm();
        return new Binary(l, r, EQUALS);
    }

    private Expression getTerm() {
        Expression expr = getSum();
        while (ind < tokens.size() && tokens.get(ind).type == SUM) {
            ind++;
            expr = new Binary(expr, getSum(), SUM);
        }
        return expr;
    }

    private Expression getSum() {
        Expression expr = getMul();
        while (ind < tokens.size() && tokens.get(ind).type == MUL) {
            ind++;
            expr = new Binary(expr, getMul(), MUL);
        }
        return expr;
    }

    private Expression getMul() {
        Expression expr = null;
        switch (tokens.get(ind++).type) {
            case VAR: {
                String name = tokens.get(ind - 1).getText();
                if (ind < tokens.size() && tokens.get(ind).type == LBRACKET) {
                    ArrayList<Expression> terms = new ArrayList<>();
                    ind++;
                    terms.add(getTerm());
                    while (tokens.get(ind).type == COMMA) {
                        ind++;
                        terms.add(getTerm());
                    }
                    ind++;
                    expr = new Function(name, terms);
                    break;
                } else {
                    expr = new Var(name);
                }
                break;
            }
            case LBRACKET:
                expr = getTerm();
                ind++;
                break;
            case ZERO:
                expr = new Zero();
                break;
        }
        while (ind < tokens.size() && tokens.get(ind).type == QUOTE) {
            ind++;
            expr = new Quote(expr);
        }
        return expr;
    }

    private List<Token> getTokensFromLine(String line) {
        List<Token> tokens = new ArrayList<>();
        while (parseInd < line.length()) {
            if (Character.isLetterOrDigit(line.charAt(parseInd))) {
                tokens.add(getNextVar(line));
            } else {
                tokens.add(getNextOp(line));
            }
        }
        return tokens;
    }

    private Token getNextVar(String line) {
        int startInd = parseInd;
        if (Character.isLetter(line.charAt(startInd))) {
            boolean isPredicate = Character.isUpperCase(line.charAt(startInd));
            while (parseInd < line.length()
                    && Character.isLetterOrDigit(line.charAt(parseInd))
                    && (Character.isUpperCase(line.charAt(parseInd)) == isPredicate)) {
                parseInd++;
            }
            return new Token(line.substring(startInd, parseInd));
        } else {
            parseInd++;
            return new Token(ZERO);
        }
    }

    private Token getNextOp(String line) {
        if (line.charAt(parseInd) == '-') {
            parseInd += 2;
            return new Token(IMPL);
        } else {
            return new Token(Type.fromString(Character.toString(line.charAt(parseInd++))));
        }
    }
}
