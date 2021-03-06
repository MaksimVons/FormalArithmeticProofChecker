import checker.*;
import expression.Expression;
import expression.parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FAProofChecker {
    public static void main(String[] args) throws IOException {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream("input.txt")));
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream("output.txt")))) {

            String firstLine[] = in.readLine().split("\\|-");

            Parser parser = new Parser();
            Set<Expression> assumptions = new HashSet<>();
            Expression assumption = parser.parse(firstLine[0], true);
            while (assumption != null) {
                assumptions.add(assumption);
                assumption = parser.parse(firstLine[0], true);
            }

            Expression toProve = parser.parse(firstLine[1]);
            List<Checker> checkers = new ArrayList<>();
            checkers.add(new AssumptionsChecker());
            checkers.add(new AxiomsChecker());
            checkers.add(new DerivationRulesChecker());

            String line = in.readLine();
            Expression expr = null;
            int ind = 1;
            while (line != null) {
                if (!line.replaceAll("\\s+", "").isEmpty()) {
                    expr = parser.parse(line);
                    CheckResult res = new CheckResult(false);
                    for (Checker checker : checkers) {
                        CheckResult r = checker.check(assumptions, expr, res.result);
                        if (r.result)
                            res = r;
                        if (!res.result && !r.error.isEmpty())
                            res = r;
                    }
                    if (!res.result) {
                        StringBuilder resString = new StringBuilder("Вывод некорректен начиная с формулы номер " + ind);
                        if (!res.error.isEmpty()) {
                            resString.append(": " + res.error + ".");
                        }
                        out.write(resString.toString() + "\n");
                        return;
                    }
                    ind++;
                }
                line = in.readLine();
            }
            if (!expr.equals(toProve)) {
                out.write("Последнее выражение не совпадает с тем, которое надо доказать.");
                return;
            }
            out.write("Доказательство корректно.");
        }
    }
}
