package com.writeoncereadmany.minimalang;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.ast.Expression;
import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.Environments;
import com.writeoncereadmany.minimalang.runtime.Evaluator;
import com.writeoncereadmany.minimalang.typechecking.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static co.unruly.control.result.Resolvers.getOrThrow;
import static java.util.stream.Collectors.joining;

public class Main {

    public static void main(String... args) throws IOException {
        OptionParser optionParser = new OptionParser();
        OptionSpec<Boolean> typechecks = optionParser.accepts("typecheck").withOptionalArg().ofType(Boolean.class).defaultsTo(true);

        OptionSet options = optionParser.parse(args);

        MinimaCompiler compiler = new MinimaCompiler();
        String inputfile = (String)options.nonOptionArguments().get(0);

        try {
            Program program = compiler.compile(Files.lines(Paths.get(inputfile)).collect(joining("\n")));

            if(options.valueOf(typechecks)) {
                Pair<Types, Expression.Catamorphism<Result<Type, List<TypeError>>, Types>> typeSystem = Typesets.justBuiltins();
                Pair<Result<Type, List<TypeError>>, Types> results = program.run(typeSystem.right, typeSystem.left);
                results.left.then(getOrThrow(TypecheckFailed::new));
            }

            program.run(Evaluator.evaluator(), Environments.justPrint(System.out::println));
        } catch (IOException ex) {
            throw new RuntimeException("Could not open file: " + inputfile);
        }
    }
}
