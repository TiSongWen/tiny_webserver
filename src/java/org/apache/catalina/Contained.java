package org.apache.catalina;

/**
 * Created by tisong on 8/10/16.
 */
public interface Contained {

    public Container getContainer();

    public void setContainer(Container container);
}
