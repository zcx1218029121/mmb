package com.mmb.beans;


public class BeanWrapper {
    private Object wrapperInstance;
    private Class<?> wrapperClass;

    public BeanWrapper(Object wrapperInstance) {
        this.wrapperClass = wrapperInstance.getClass();
        this.wrapperInstance = wrapperInstance;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }
}
