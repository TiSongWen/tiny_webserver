package org.apache.catalina;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by tisong on 8/9/16.
 */
public interface Value {

    // -------------------------------------------------- Public Methods

    public void invoke(Request request, Response response, ValueContext valueContext) throws IOException, ServletException;
}

