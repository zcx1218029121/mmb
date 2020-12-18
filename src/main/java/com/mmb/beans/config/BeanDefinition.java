package com.mmb.beans.config;

/**
 *   @author loafer
 */
public class BeanDefinition {
    private String factoryBeanName;
    private String beanClassName;

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "factoryBeanName='" + factoryBeanName + '\'' +
                ", beanClassName='" + beanClassName + '\'' +
                '}';
    }
}
