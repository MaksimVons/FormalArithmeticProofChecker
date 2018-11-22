package expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Zero implements Expression {

    @Override
    public Expression substitute(Var var, Expression expr) {
        return this;
    }

    @Override
    public boolean areVarsFreeInPlaceOf(Set<Var> linkedVars, Set<Var> freeVars, Var place) {
        return true;
    }

    @Override
    public Set<Var> getFreeVars(Set<Var> linkedVars) {
        return new HashSet<>();
    }

    @Override
    public String toString() {
        return "0";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Zero)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Type.ZERO);
    }
}
