package checker;

import expression.*;
import expression.parser.Parser;

import java.util.*;

public class AxiomsChecker implements Checker {
    private static String[] classicAxiomsStrings = {
            "A->B->A",
            "(A->B)->(A->B->C)->(A->C)",
            "A->B->A&B",
            "A&B->A",
            "A&B->B",
            "A->A|B",
            "B->A|B",
            "(A->C)->(B->C)->(A|B->C)",
            "(A->B)->(A->!B)->!A",
            "!!A->A"
    };

    private static String[] FAAxiomsStrings = {
            "a=b->a'=b'",
            "a=b->a=c->b=c",
            "a'=b'->a=b",
            "!a'=0",
            "a+b'=(a+b)'",
            "a+0=a",
            "a*0=0",
            "a*b'=a*b+a"
    };

    private static List<Expression> classicAxioms;
    private static List<Expression> FAAxioms;

    static {
        classicAxioms = new ArrayList<>();
        for (String expr : classicAxiomsStrings) {
            classicAxioms.add(new Parser().parse(expr, false));
        }

        FAAxioms = new ArrayList<>();
        for (String expr : FAAxiomsStrings) {
            FAAxioms.add(new Parser().parse(expr, false));
        }
    }

    @Override
    public CheckResult check(Set<Expression> assumptions, Expression expr) {
        for (Expression axiom : classicAxioms) {
            if (checkAxiom(expr, axiom, new HashMap<>()))
                return new CheckResult(true, "");
        }

        CheckResult res = null;
        // ∀x(ψ) → (ψ[x := θ]) , θ free for substitution in place of x
        if (expr instanceof Binary) {
            if (((Binary) expr).type == Type.IMPL
                    && ((Binary) expr).l instanceof Binary
                    && ((Binary) ((Binary) expr).l).type == Type.FORALL) {

                Binary left = (Binary) ((Binary) expr).l;
                Expression right = ((Binary) expr).r;
                Var var = (Var) left.l;
                HashMap<Expression, Expression> map = new HashMap<>();
                if (checkAxiom(right, left.r, map)) {
                    if (!map.containsKey(var))
                        return new CheckResult(true, "");
                    Expression theta = map.get(var);
                    if (left.r.areVarsFreeInPlaceOf(new HashSet<>(), theta.getFreeVars(new HashSet<>()), var)) {
                        return new CheckResult(true, "");
                    }
                    res = new CheckResult(false, "терм " + theta.toString()
                            + " не свободен для подстановки в формулу " + left.r.toString()
                            + " вместо переменной " + var.toString());
                }
            }
        }
        // (ψ[x := θ]) → ∃x(ψ) , θ free for substitution in place of x
        if (expr instanceof Binary) {
            if (((Binary) expr).type == Type.IMPL
                    && ((Binary) expr).r instanceof Binary
                    && ((Binary) ((Binary) expr).r).type == Type.EXISTS) {

                Binary right = (Binary) ((Binary) expr).r;
                Expression left = ((Binary) expr).l;
                Var var = (Var) right.l;
                HashMap<Expression, Expression> map = new HashMap<>();
                if (checkAxiom(left, right.r, map)) {
                    if (!map.containsKey(var))
                        return new CheckResult(true, "");
                    Expression theta = map.get(var);
                    if (right.r.areVarsFreeInPlaceOf(new HashSet<>(), theta.getFreeVars(new HashSet<>()), var)) {
                        return new CheckResult(true, "");
                    }
                    res = new CheckResult(false, "терм " + theta.toString()
                            + " не свободен для подстановки в формулу " + right.r.toString()
                            + " вместо переменной " + var.toString());
                }
            }
        }

        for (Expression axiom : FAAxioms) {
            if (axiom.equals(expr))
                return new CheckResult(true, "");
        }

        // FA axiom 9 : (ψ[x := 0]) & ∀x((ψ) → (ψ)[x := x']) → (ψ) and x is in free vars of ψ
        if (expr instanceof Binary && ((Binary) expr).type == Type.IMPL) {
            Expression l = ((Binary) expr).l;
            Expression psi = ((Binary) expr).r;
            if (l instanceof Binary && ((Binary) l).type == Type.CONJ) {
                Expression psi0 = ((Binary) l).l;
                Expression r = ((Binary) l).r;
                if (r instanceof Binary && ((Binary) r).type == Type.FORALL) {
                    Var var = (Var) ((Binary) r).l;
                    if (((Binary) r).r instanceof Binary && ((Binary) ((Binary) r).r).type == Type.IMPL) {
                        if (psi.equals(((Binary) ((Binary) r).r).l)) {
                            if (psi.substitute(var, new Quote(var)).equals(((Binary) ((Binary) r).r).r)) {
                                return new CheckResult(true, "");
                            }
                        }
                    }
                }
            }
        }

        if (res != null) return res;
        return new CheckResult(false, "");
    }

    private boolean checkAxiom(Expression expr, Expression axiom, Map<Expression, Expression> varsInAxiom) {
        if (expr instanceof Binary && axiom instanceof Binary) {
            if (((Binary) expr).type != ((Binary) axiom).type)
                return false;
            return checkAxiom(((Binary) expr).l, ((Binary) axiom).l, varsInAxiom) &&
                    checkAxiom(((Binary) expr).r, ((Binary) axiom).r, varsInAxiom);
        }
        if (axiom instanceof Var || (axiom instanceof Predicate && ((Predicate) axiom).terms.size() == 0)) {
            if (varsInAxiom.containsKey(axiom)) {
                if (varsInAxiom.get((axiom)).equals(expr))
                    return true;
                return false;
            }
            varsInAxiom.put((axiom), expr);
            return true;
        }
        if (axiom instanceof Zero && expr instanceof Zero)
            return true;
        if (axiom instanceof Quote && expr instanceof Quote)
            return checkAxiom(((Quote) expr).expression,
                    ((Quote) axiom).expression,
                    varsInAxiom);
        if (axiom instanceof Negate && expr instanceof Negate) {
            return checkAxiom(((Negate) expr).expression,
                    ((Negate) axiom).expression,
                    varsInAxiom);
        }
        if (axiom instanceof Function && expr instanceof Function) {
            if (!((Function) axiom).name.equals(((Function) expr).name))
                return false;
            if (((Function) axiom).terms.size() != ((Function) expr).terms.size())
                return false;

            for (int i = 0; i < ((Function) axiom).terms.size(); i++) {
                if (!checkAxiom(((Function) expr).terms.get(i),
                        ((Function) axiom).terms.get(i),
                        varsInAxiom))
                    return false;
            }
            return true;
        }
        if (axiom instanceof Predicate && expr instanceof Predicate) {
            if (!((Predicate) axiom).name.equals(((Predicate) expr).name))
                return false;
            if (((Predicate) axiom).terms.size() != ((Predicate) expr).terms.size())
                return false;

            for (int i = 0; i < ((Predicate) axiom).terms.size(); i++) {
                if (!checkAxiom(((Predicate) expr).terms.get(i),
                        ((Predicate) axiom).terms.get(i),
                        varsInAxiom))
                    return false;
            }
            return true;
        }
        return false;
    }
}
