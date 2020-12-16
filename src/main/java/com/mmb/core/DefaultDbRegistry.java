package com.mmb.core;

import com.mmb.beans.BeanWrapper;
import com.mmb.beans.config.BeanDefinition;
import com.mmb.beans.support.DaoDefinitionReader;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认db 层的实现
 * 所有db服务都是单例的
 */
public enum DefaultDbRegistry {
    // 枚举单例
    instance;

    public DefaultDbRegistry init(String scanPackage, SqlSessionFactory sqlSessionFactory) {
        DaoDefinitionReader daoDefinitionReader = new DaoDefinitionReader(scanPackage);
        try {
            doRegisterBeanDefinition(daoDefinitionReader.loadBeanDefinitions());
            doCreateBean();
            this.sqlSessionFactory = sqlSessionFactory;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();
    private SqlSessionFactory sqlSessionFactory;
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private final Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();


    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The" + beanDefinition.getFactoryBeanName() + "is exists!!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            this.beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = instanceBean(beanName, beanDefinition);
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        populateBean(beanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    private void doCreateBean() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }

    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    private Object instanceBean(String beanName, BeanDefinition beanDefinition) {
        if (factoryBeanObjectCache.containsKey(beanName)) {
            return factoryBeanObjectCache.get(beanName);
        }
        String className = beanDefinition.getBeanClassName();
        Object instance = null;

        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
        factoryBeanObjectCache.put(beanName, instance);

        return instance;
    }

    private void populateBean(BeanWrapper beanWrapper) {

        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrapperClass();

        //只有继承了BaseService 才需要实例化
        if (!BaseService.class.isAssignableFrom(clazz)) {
            return;
        }
        for (Field field : clazz.getSuperclass().getDeclaredFields()) {
            if ("sqlSessionFactory".equals(field.getName())) {
                field.setAccessible(true);
                try {
                    field.set(instance, sqlSessionFactory);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}