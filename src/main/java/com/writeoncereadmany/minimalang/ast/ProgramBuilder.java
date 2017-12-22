package com.writeoncereadmany.minimalang.ast;

import co.unruly.control.pair.Maps;
import com.writeoncereadmany.minimalang.generated.MinimalangParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.stream.Stream;

import static co.unruly.control.HigherOrderFunctions.zip;
import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static com.writeoncereadmany.minimalang.ast.Expression.*;
import static com.writeoncereadmany.minimalang.ast.TypeDefinition.*;
import static com.writeoncereadmany.minimalang.util.ListUtils.allButLast;
import static com.writeoncereadmany.minimalang.util.ListUtils.last;
import static com.writeoncereadmany.minimalang.util.StringUtils.stripSurroundingQuotes;
import static java.util.stream.Collectors.toList;

public interface ProgramBuilder {

    static Program build(MinimalangParser.ProgramContext program) {
        return new Program(sequence(program
            .expression()
            .stream()
            .map(ProgramBuilder::buildExpression)
            .collect(toList())));
    }

    static Expression buildExpression(MinimalangParser.ExpressionContext expression) {
        return matchValue(expression,
            ifType(MinimalangParser.StringContext.class, str ->
                stringLiteral(stripSurroundingQuotes(str.STRING_LITERAL().getText()))),
            ifType(MinimalangParser.NumberContext.class, num ->
                numberLiteral(num.NUMBER_LITERAL().getText())),
            ifType(MinimalangParser.DeclarationContext.class, dec ->
                declaration(
                    buildIntroduction(dec.introduction()),
                    buildExpression(dec.expression()))),
            ifType(MinimalangParser.VariableContext.class, var ->
                variable(var.IDENTIFIER().getText())),
            ifType(MinimalangParser.ObjectContext.class, obj ->
                objectLiteral(zip(
                    obj.introduction().stream().map(ProgramBuilder::buildIntroduction),
                    obj.expression().stream().map(exp -> buildExpression(exp))).collect(toList()))),
            ifType(MinimalangParser.AccessContext.class, acc ->
                access(
                    buildExpression(acc.expression()),
                    acc.IDENTIFIER().getText())),
            ifType(MinimalangParser.FunctionContext.class, fun ->
                function(
                    fun.introduction().stream().map(ProgramBuilder::buildIntroduction).collect(toList()),
                    buildExpression(fun.expression())
                )),
            ifType(MinimalangParser.CallContext.class, call ->
                call(buildExpression(
                    call.expression(0)),
                    call.expression()
                        .subList(1, call.expression().size())
                        .stream()
                        .map(ProgramBuilder::buildExpression)
                        .collect(toList()))),
            ifType(MinimalangParser.SequenceContext.class, seq ->
                sequence(seq.expression()
                    .stream()
                    .map(ProgramBuilder::buildExpression)
                    .collect(toList()))),
            ifType(MinimalangParser.TypedefinitionContext.class, type ->
                typeDeclaration(type.IDENTIFIER().getText(), buildTypeDefinition(type.type())))
        ).otherwise(exp -> {
            throw new RuntimeException("Failed to find an implementation for " + exp.getClass());
        });
    }

    static TypeDefinition buildTypeDefinition(MinimalangParser.TypeContext type) {
        return matchValue(type,
            ifType(MinimalangParser.Named_typeContext.class, namedType ->
                namedTypeDefinition(namedType.IDENTIFIER().getText())),
            ifType(MinimalangParser.Function_typeContext.class, functionType -> {
                List<TypeDefinition> types = functionType.type().stream().map(ProgramBuilder::buildTypeDefinition).collect(toList());
                return functionTypeDefinition(allButLast(types), last(types));
            }),
            ifType(MinimalangParser.Interface_typeContext.class, interfaceType -> {
                Stream<String> fieldNames = interfaceType.IDENTIFIER().stream().map(ParseTree::getText);
                Stream<TypeDefinition> fieldTypes = interfaceType.type().stream().map(ProgramBuilder::buildTypeDefinition);
                return interfaceTypeDefinition(zip(fieldNames, fieldTypes).collect(Maps.toMap()));
            })
        ).otherwise(typ -> {
            throw new RuntimeException("Failed to find an implementation for " + typ.getClass());
        });
    }

    static Introduction buildIntroduction(MinimalangParser.IntroductionContext ctx) {
        return new Introduction(
            ctx.ANNOTATION()
                .stream()
                .map(ParseTree::getText)
                .map(x -> x.substring(1))
                .collect(toList()),
            ctx.IDENTIFIER().getText());
    }
}
