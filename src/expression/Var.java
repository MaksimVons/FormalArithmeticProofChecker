package expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Var implements Expression {
    public final String name;

    public Var(String name) {
        this.name = name;
    }

    @Override
    public Expression substitute(Var var, Expression expr) {
        if (this.equals(var)) return expr;
        return this;
    }

    @Override
    public boolean areVarsFreeInPlaceOf(Set<Var> linkedVars, Set<Var> freeVars, Var place) {
        if (linkedVars.contains(place))
            return true;
        if (this.equals(place)) {
            for (Var var : freeVars) {
                if (linkedVars.contains(var))
                    return false;
            }
        }
        return true;
    }

    @Override
    public Set<Var> getFreeVars(Set<Var> linkedVars) {
        if (!linkedVars.contains(this)) {
            Set<Var> set = new HashSet<>();
            set.add(this);
            return set;
        }
        return new HashSet<>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Var && ((Var) obj).name.equals(name);
    }
}