package org.sudu.experiments.parser.activity.graph.expr;

import org.sudu.experiments.parser.activity.graph.IExpr;
import org.sudu.experiments.parser.activity.graph.Path;

import java.util.ArrayList;
import java.util.List;

public class CommaExpr implements IExpr {
    public final List<ConsExpr> exprs = new ArrayList<>();

    @Override
    public String toString() {
        if (exprs.size() == 1 && exprs.get(0).exprs.size() == 1)
            return exprs.get(0).toString();
        else
            return "{" + String.join(", ", exprs.stream().map(ConsExpr::toString).toList()) + "}";
    }

    @Override
    public boolean check(Path path, int from) {
        return checkPos(path, from) >= 0;
    }

    int checkPos(Path path, int from) {
        for (var e: exprs) {
            from = e.checkPos(path, from);
            if (from < 0)
                return -1;
        }
        return from;
    }
}
