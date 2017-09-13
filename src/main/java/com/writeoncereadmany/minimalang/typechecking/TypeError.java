package com.writeoncereadmany.minimalang.typechecking;

import java.util.Objects;

public class TypeError {

    private final String reason;

    public TypeError(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeError typeError = (TypeError) o;
        return Objects.equals(reason, typeError.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason);
    }

    @Override
    public String toString() {
        return "TypeError{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
