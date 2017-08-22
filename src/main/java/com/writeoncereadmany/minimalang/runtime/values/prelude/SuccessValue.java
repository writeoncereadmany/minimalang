package com.writeoncereadmany.minimalang.runtime.values.prelude;

import com.writeoncereadmany.minimalang.runtime.values.Value;

/**
 * Created by tomj on 22/08/2017.
 */
public enum SuccessValue implements Value {

    SUCCESS;

    @Override
    public String show() {
        return "SUCCESS";
    }
}
