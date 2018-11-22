package checker;

import expression.Expression;

import java.util.Set;

public interface Checker {
    CheckResult check(Set<Expression> assumptions, Expression expr);
}
