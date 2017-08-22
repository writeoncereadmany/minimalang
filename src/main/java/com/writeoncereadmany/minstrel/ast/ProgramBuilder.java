package com.writeoncereadmany.minstrel.ast;

import com.writeoncereadmany.minstrel.ast.expressions.Expression;
import com.writeoncereadmany.minstrel.ast.misc.Arguments;
import com.writeoncereadmany.minstrel.generated.MinstrelParser;


import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static com.writeoncereadmany.minstrel.ast.expressions.Expression.call;
import static com.writeoncereadmany.minstrel.ast.expressions.Expression.stringLiteral;
import static com.writeoncereadmany.minstrel.ast.expressions.Expression.variable;
import static java.util.stream.Collectors.toList;

public interface ProgramBuilder {

    static Program build(MinstrelParser.ProgramContext program) {
        return new Program(buildExpression(program.expression()));
    }

    static Expression buildExpression(MinstrelParser.ExpressionContext expression) {
        return matchValue(expression,
                ifType(MinstrelParser.CallContext.class, call -> call(buildExpression(call.expression()), buildArguments(call.args()))),
                ifType(MinstrelParser.VariableContext.class, var -> variable(var.IDENTIFIER().getText())),
                ifType(MinstrelParser.StringContext.class, str -> stringLiteral(str.STRING_LITERAL().getText()))
        ).otherwise(__ -> { throw new RuntimeException("Failed to find an implementation"); });
    }

    static Arguments buildArguments(MinstrelParser.ArgsContext args) {
        return new Arguments(args.expression().stream().map(ProgramBuilder::buildExpression).collect(toList()));
    }
}
