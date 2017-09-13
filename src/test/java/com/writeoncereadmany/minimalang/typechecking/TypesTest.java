package com.writeoncereadmany.minimalang.typechecking;

import org.junit.jupiter.api.Test;

import static co.unruly.control.matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertThat;

public class TypesTest {

    @Test
    public void aDataTypeResolvesToItself() {
        Types types = new Types();
        DataType color = new DataType("Color");

        assertThat(types.resolve(color), isSuccessOf(color));
    }

    @Test
    public void aNonexistentNamedTypeResolvesToFailure() {
        Types types = new Types();

        assertThat(types.resolve(new NamedType("Shape")), isFailureOf(new TypeError("Type Shape not defined")));
    }

    @Test
    public void aNamedTypeResolvesToWhateverDefinedIt() {
        DataType rhombus = new DataType("Rhombus");
        Types types = new Types().withNamedType("Diamond", rhombus);

        assertThat(types.resolve(new NamedType("Diamond")), isSuccessOf(rhombus));
    }

    @Test
    public void aChainOfNamedTypesResolveToUltimateNonNameType() {
        DataType sandwich = new DataType("Sandwich");
        NamedType sub = new NamedType("Sub");
        NamedType baguette = new NamedType("Baguette");

        Types types = new Types()
                .withNamedType("Sub", sandwich)
                .withNamedType("Baguette", sub)
                .withNamedType("Panini", baguette);

        assertThat(types.resolve(new NamedType("Panini")), isSuccessOf(sandwich));
    }

}