package com.writeoncereadmany.minimalang.ast;

import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.ast.misc.Arguments;
import com.writeoncereadmany.minimalang.generated.MinimalangParser;

import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.*;
import static com.writeoncereadmany.minimalang.util.StringUtils.stripSurroundingQuotes;
import static java.util.stream.Collectors.toList;

public interface ProgramBuilder {

    static Program build(MinimalangParser.ProgramContext program) {
        return new Program(buildExpression(program.expression()));
    }

    static Expression buildExpression(MinimalangParser.ExpressionContext expression) {
        return matchValue(expression,
                ifType(MinimalangParser.CallContext.class, call -> call(buildExpression(call.expression()), buildArguments(call.args()))),
                ifType(MinimalangParser.VariableContext.class, var -> variable(var.IDENTIFIER().getText())),
                ifType(MinimalangParser.StringContext.class, str -> stringLiteral(stripSurroundingQuotes(str.STRING_LITERAL().getText()))),
                ifType(MinimalangParser.SequenceContext.class, seq ->
                    sequence(seq.expression()
                            .stream()
                            .map(ProgramBuilder::buildExpression)
                            .collect(toList())))
        ).otherwise(__ -> { throw new RuntimeException("Failed to find an implementation"); });
    }

    static Arguments buildArguments(MinimalangParser.ArgsContext args) {
        return new Arguments(args.expression().stream().map(ProgramBuilder::buildExpression).collect(toList()));
    }
}
