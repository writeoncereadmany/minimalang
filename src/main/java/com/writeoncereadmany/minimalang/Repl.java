package com.writeoncereadmany.minimalang;

import co.unruly.control.pair.Pair;
import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.Evaluator;
import com.writeoncereadmany.minimalang.runtime.values.Value;
import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;

import java.util.Scanner;

public class Repl {

    public static void main(String... args) {
        final Scanner scanner = new Scanner(System.in);
        final Compiler compiler = new Compiler();
        Environment environment = new Environment().with("print", new PrintFunction(System.out::println));

        while(true) {
            System.out.print(" > ");
            String nextLine = scanner.nextLine();
            Program nextProgram = compiler.compile(nextLine);
            Pair<Value, Environment> result = nextProgram.run(Evaluator.evaluator(), environment);
            System.out.println(result.left.show());
            environment = result.right.with("it", result.left);
        }
    }
}
