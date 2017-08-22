package com.writeoncereadmany.minimalang.runtime;

import com.writeoncereadmany.minimalang.generated.MinimalangParser;

/**
 * Created by tomj on 22/08/2017.
 */
public class MinimalangExecutionException extends RuntimeException {

    public MinimalangExecutionException(String msg) {
        super(msg);
    }
}
