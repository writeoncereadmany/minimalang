package com.writeoncereadmany.minimalang.ast;

import co.unruly.control.PartialApplication;
import co.unruly.control.pair.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface CataFunctions {

    /*************************************
     *
     * Catamorphism functional interfaces
     *
     *************************************/

    @FunctionalInterface
    interface Interpreter<X, T, C> {
        Pair<T, C> apply(X x, C context);
    }

    @FunctionalInterface
    interface BiInterpreter<X, Y, T, C>  {
        Pair<T, C> apply(X x, Y y, C context);
    }

    @FunctionalInterface
    interface TriInterpreter<X, Y, Z, T, C>  {
        Pair<T, C> apply(X x, Y y, Z z, C context);
    }

    @FunctionalInterface
    interface QuadFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }

    /*************************************
     *
     * Catamorphism context-free helpers
     *
     *************************************/

    static <E, T, C> Interpreter<E, T, C> contextFree(Function<E, T> contextFreeFunction) {
        return (e, c) -> Pair.of(contextFreeFunction.apply(e), c);
    }

    static <A, B, T, C> BiInterpreter<A, B, T, C> contextFree(BiFunction<A, B, T> contextFreeFunction) {
        return (a, b, c) -> Pair.of(contextFreeFunction.apply(a, b), c);
    }

    static <X, Y, Z, T, C> TriInterpreter<X, Y, Z, T, C> contextFree(PartialApplication.TriFunction<X, Y, Z, T> contextFreeFunction) {
        return (x, y, z, c) -> Pair.of(contextFreeFunction.apply(x,y,z), c);
    }

    static <E, T, C> Interpreter<E, T, C> usingContext(BiFunction<E, C, T> contextUsingFunction) {
        return (e, c) -> Pair.of(contextUsingFunction.apply(e, c), c);
    }

    static <A, B, T, C> BiInterpreter<A, B, T, C> usingContext(PartialApplication.TriFunction<A, B, C, T> contextUsingFunction) {
        return (a, b, c) -> Pair.of(contextUsingFunction.apply(a, b, c), c);
    }

    static <X, Y, Z, C, T> TriInterpreter<X, Y, Z, T, C> usingContext(QuadFunction<X, Y, Z, C, T> contextUsingFunction) {
        return (x, y, z, c) -> Pair.of(contextUsingFunction.apply(x, y, z, c), c);
    }
}
