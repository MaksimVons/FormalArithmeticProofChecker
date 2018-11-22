package checker;

import expression.Binary;
import expression.Expression;
import expression.Type;
import expression.Var;

import java.util.*;

public class DerivationRulesChecker implements Checker {
    Set<Expression> expressions = new HashSet<>();
    Set<Expression> MPProoved = new HashSet<>();
    Map<Expression, List<Expression>> MPForProoving = new HashMap<>();

    @Override
    public CheckResult check(Set<Expression> assumptions, Expression expr) {
        expressions.add(expr);
        if (MPForProoving.containsKey(expr)) {
            List<Expression> proovedByMp = MPForProoving.get(expr);
            for (Expression e : proovedByMp) {
                MPProoved.add(e);
            }
            MPForProoving.remove(expr);
        }
        if (expr instanceof Binary && ((Binary) expr).type == Type.IMPL) {
            Expression l = ((Binary) expr).l;
            Expression r = ((Binary) expr).r;
            if (expressions.contains(l)) {
                MPProoved.add(r);
            } else {
                if (MPForProoving.containsKey(l)) {
                    MPForProoving.get(l).add(r);
                } else {
                    List<Expression> newList = new ArrayList<>();
                    newList.add(r);
                    MPForProoving.put(l, newList);
                }
            }
        }
        if (MPProoved.contains(expr)) {
            expressions.add(expr);
            return new CheckResult(true, "");
        }

        if (expr instanceof Binary && ((Binary) expr).type == Type.IMPL) {
            // (φ) → (ψ) |- (φ) → ∀x(ψ) if x not in freeVars of φ
            if (((Binary) expr).r instanceof Binary &&
                    ((Binary) ((Binary) expr).r).type == Type.FORALL) {
                Expression phi = ((Binary) expr).l;
                Expression psi = ((Binary) ((Binary) expr).r).r;
                Var x = (Var) ((Binary) ((Binary) expr).r).l;
                if (expressions.contains(new Binary(phi, psi, Type.IMPL))) {
                    if (phi.getFreeVars(new HashSet<>()).contains(x)) {
                        return new CheckResult(false,
                                "переменная " + x.name + " входит свободно в формулу " + phi.toString());
                    }
                    return new CheckResult(true, "");
                }
            }
            // (ψ) → (φ) |- ∃x(ψ) → (φ) if x not in freeVars of φ and assumptions
            if (((Binary) expr).l instanceof Binary &&
                    ((Binary) ((Binary) expr).l).type == Type.EXISTS) {
                Expression phi = ((Binary) expr).r;
                Expression psi = ((Binary) ((Binary) expr).l).r;
                Var x = (Var) ((Binary) ((Binary) expr).l).l;
                if (expressions.contains(new Binary(psi, phi, Type.IMPL))) {
                    if (phi.getFreeVars(new HashSet<>()).contains(x)) {
                        return new CheckResult(false,
                                "переменная " + x.name + " входит свободно в формулу " + phi.toString());
                    }
                    return new CheckResult(true, "");
                }
            }
        }
        return new CheckResult(false, "");
    }
}
