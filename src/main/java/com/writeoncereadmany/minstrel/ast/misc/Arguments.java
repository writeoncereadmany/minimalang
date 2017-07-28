package com.writeoncereadmany.minstrel.ast.misc;

import com.writeoncereadmany.minstrel.ast.expressions.Expression;
import com.writeoncereadmany.minstrel.generated.MinstrelParser;

import java.util.List;

/**
 * Created by tomj on 27/07/2017.
 */
public class Arguments {

    private List<Expression> expressions;

    public Arguments(List<Expression> expressions) {
        this.expressions = expressions;
    }
}
