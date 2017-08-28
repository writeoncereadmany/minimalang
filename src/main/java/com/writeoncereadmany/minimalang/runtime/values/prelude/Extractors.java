package com.writeoncereadmany.minimalang.runtime.values.prelude;

import co.unruly.control.result.Resolvers;
import com.writeoncereadmany.minimalang.runtime.EvaluationException;
import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.List;
import java.util.function.Function;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.castTo;
import static co.unruly.control.result.Transformers.*;
import static com.writeoncereadmany.minimalang.runtime.values.prelude.Extractors.Empty.EMPTY;
import static com.writeoncereadmany.minimalang.util.ListUtils.extractSingleValue;

public interface Extractors {

    enum Empty {
        EMPTY;
    }

    static <T extends Value> Function<List<Value>, T> singleParamOfType(Class<T> type) {
        return values -> startWith(values)
            .then(extractSingleValue())
            .then(onFailure(args -> "Expected one argument, got " + args.size()))
            .then(onSuccess(castTo(type)))
            .then(onSuccess(onFailure(val -> "Expected type " + type + ", got " + val.getClass())))
            .then(mergeFailures()).then(Resolvers.getOrThrow(EvaluationException::new));
    }

    static Function<List<Value>, Empty> noParams() {
        return values -> {
            if (values.isEmpty()) {
                return EMPTY;
            } else {
                throw new EvaluationException("Expected no arguments: got " + values.size());
            }
        };
    }
}
