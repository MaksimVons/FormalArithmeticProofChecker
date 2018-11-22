package expression;

import java.util.*;

public class Function implements Expression {
    public final String name;
    public List<Expression> terms;

    public Function(String name, List<Expression> terms) {
        this.name = name;
        this.terms = terms;
    }

    @Override
    public Expression substitute(Var var, Expression expr) {
        List<Expression> newTerms = new ArrayList<>();
        terms.forEach(term->newTerms.add(term.substitute(var, expr)));
        return new Function(name, newTerms);
    }

    @Override
    public boolean areVarsFreeInPlaceOf(Set<Var> linkedVars, Set<Var> freeVars, Var place) {
        for (Expression term : terms) {
            if (!term.areVarsFreeInPlaceOf(linkedVars, freeVars, place))
                return false;
        }
        return true;
    }

    @Override
    public Set<Var> getFreeVars(Set<Var> linkedVars) {
        Set<Var> set = new HashSet<>();
        terms.forEach(term -> set.addAll(term.getFreeVars(linkedVars)));
        return set;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(name);
        if (!terms.isEmpty()) {
            res.append("(");
            res.append(terms.get(0));
            for (int i = 1; i < terms.size(); i++) {
                res.append("," + terms.get(i).toString());
            }
            res.append(")");
        }
        return res.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Function)) {
            return false;
        } else {
            Function that = (Function) obj;
            return name.equals(that.name) && terms.equals(that.terms);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, terms);
    }
}
