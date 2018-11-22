package expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Quote implements Expression {
    public Expression expression;

    public Quote(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Expression substitute(Var var, Expression expr) {
        return new Quote(expression.substitute(var, expr));
    }

    @Override
    public boolean areVarsFreeInPlaceOf(Set<Var> linkedVars, Set<Var> freeVars, Var place) {
        return expression.areVarsFreeInPlaceOf(linkedVars, freeVars, place);
    }

    @Override
    public Set<Var> getFreeVars(Set<Var> linkedVars) {
        return expression.getFreeVars(linkedVars);
    }

    @Override
    public String toString() {
        return expression.toString() + "'";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Quote)) {
            return false;
        } else {
            Quote that = (Quote) obj;
            return expression.equals(that.expression);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(Type.QUOTE, expression);
    }
}