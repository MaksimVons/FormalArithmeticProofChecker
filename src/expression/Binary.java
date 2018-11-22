package expression;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Binary implements Expression {
    public Expression l, r;
    public Type type;

    public Binary(Expression l, Expression r, Type sign) {
        this.l = l;
        this.r = r;
        this.type = sign;
    }

    @Override
    public Expression substitute(Var var, Expression expr) {
        return new Binary(
                l.substitute(var, expr),
                r.substitute(var, expr),
                type);
    }

    @Override
    public boolean areVarsFreeInPlaceOf(Set<Var> linkedVars, Set<Var> freeVars, Var place) {
        if (type == Type.FORALL || type == Type.EXISTS) {
            Set<Var> thisLinkedVars = new HashSet<>(linkedVars);
            thisLinkedVars.add((Var) l);
            return r.areVarsFreeInPlaceOf(thisLinkedVars, freeVars, place);
        }
        return l.areVarsFreeInPlaceOf(linkedVars, freeVars, place) && r.areVarsFreeInPlaceOf(linkedVars, freeVars, place);
    }

    @Override
    public Set<Var> getFreeVars(Set<Var> linkedVars) {
        if (type == Type.FORALL || type == Type.EXISTS) {
            linkedVars.add((Var) l);
            return r.getFreeVars(linkedVars);
        }
        Set<Var> freeVars = l.getFreeVars(linkedVars);
        freeVars.addAll(r.getFreeVars(linkedVars));
        return freeVars;
    }

    @Override
    public String toString() {
        switch (type) {
            case FORALL:
            case EXISTS:
                return "(" + type.getText() + l.toString() + r.toString() + ")";
        }
        return "(" + l.toString() + type.getText() + r.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Binary)) {
            return false;
        } else {
            Binary that = (Binary) obj;
            return type.equals(that.type) && l.equals(that.l) && r.equals(that.r);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(l, type, r);
    }

}