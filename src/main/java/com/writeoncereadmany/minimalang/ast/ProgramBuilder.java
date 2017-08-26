package com.writeoncereadmany.minimalang.ast;

import co.unruly.control.HigherOrderFunctions;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.generated.MinimalangParser;
import org.antlr.v4.runtime.tree.ParseTree;

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
                ifType(MinimalangParser.CallContext.class, call ->
                    call(buildExpression(
                        call.expression(0)),
                        call.expression()
                            .subList(1, call.expression().size())
                            .stream()
                            .map(ProgramBuilder::buildExpression)
                            .collect(toList()))),
                ifType(MinimalangParser.VariableContext.class, var -> variable(var.IDENTIFIER().getText())),
                ifType(MinimalangParser.StringContext.class, str -> stringLiteral(stripSurroundingQuotes(str.STRING_LITERAL().getText()))),
                ifType(MinimalangParser.SequenceContext.class, seq -> sequence(buildExpression(seq.expression(0)), buildExpression(seq.expression(1)))),
                ifType(MinimalangParser.DeclarationContext.class, dec -> declaration(dec.IDENTIFIER().getText(), buildExpression(dec.expression()))),
                ifType(MinimalangParser.ObjectContext.class, obj ->
                    objectLiteral(HigherOrderFunctions.zip(
                        obj.IDENTIFIER().stream().map(ParseTree::getText),
                        obj.expression().stream().map(exp -> buildExpression(exp))).collect(toList()))),
                ifType(MinimalangParser.AccessContext.class, acc -> access(buildExpression(acc.expression()), acc.IDENTIFIER().getText()))
        ).otherwise(exp -> { throw new RuntimeException("Failed to find an implementation for " + exp.getClass()); });
    }
}
