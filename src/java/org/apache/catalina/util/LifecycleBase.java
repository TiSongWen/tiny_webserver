package org.apache.catalina.util;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;

/**
 * Created by tisong on 8/18/16.
 */
public abstract class LifecycleBase implements Lifecycle{


    private LifecycleSupport lifecycle = new LifecycleSupport(this);

    private volatile LifecycleState state = LifecycleState.NEW;


    @Override
    public final void init() throws LifecycleException{

        if (!state.equals(LifecycleState.NEW)) {
            invalidTransition(Lifecycle.BEFORE_INIT_EVENT);
        }

        setStateInternal(LifecycleState.INITIALIZING, null, false);

        try {
            initInternal();
        } catch (Throwable t) {
            setStateInternal(LifecycleState.FAILED, null, false);
            throw new LifecycleException("");
        }

        setStateInternal(LifecycleState.INITIALIZED, null, false);
    }


    protected abstract void initInternal() throws LifecycleException;

    @Override
    public final synchronized void start() throws LifecycleException {


        startInternal();
    }

    protected abstract void startInternal() throws LifecycleException;

    protected void setState(LifecycleState state) throws LifecycleException {
        setStateInternal(state, null,  true);
    }

    /**
     * 设置生命周期的当前状态, 并触发事件
     * @param state
     * @param data
     * @param check
     */
    private void setStateInternal(LifecycleState state, Object data, boolean check) {


        if (check) {

        }

        this.state = state;

        String lifecycleEvent = state.getLifecycleEvent();
        if (lifecycleEvent != null) {
            fireLifecycleEvent(lifecycleEvent, data);
        }
    }

    protected void fireLifecycleEvent(String type, Object data) {
        lifecycle.fireLifecycleEvent(type, data);
    }
}