package checker;

import expression.Expression;

import java.util.Set;

public class AssumptionsChecker implements Checker {
    @Override
    public CheckResult check(Set<Expression> assumptions, Expression expr) {
        return new CheckResult(assumptions.contains(expr), "");
    }
}
