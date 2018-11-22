package expression;

import java.util.Set;

public interface Expression {
    String toString();

    boolean equals(Object other);

    Expression substitute(Var var, Expression expr);

    boolean areVarsFreeInPlaceOf(Set<Var> linkedVars, Set<Var> freeVars, Var place);

    Set<Var> getFreeVars(Set<Var> linkedVars);
}
