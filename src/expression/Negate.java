package expression;

import java.util.Objects;
import java.util.Set;

public class Negate implements Expression {
    public Expression expression;

    public Negate(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Expression substitute(Var var, Expression expr) {
        return new Negate(expression.substitute(var, expr));
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
        return "!" + expression.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || !(obj instanceof Negate)) {
            return false;
        } else {
            return expression.equals(((Negate) obj).expression);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(Type.NOT, expression);
    }
}