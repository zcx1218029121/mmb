package com.mmb.beans.support;

import com.mmb.beans.config.BeanDefinition;
import com.mmb.core.scan.ScanExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author loafer
 */
public class DaoDefinitionReader {
    private String scanPackage;


    public DaoDefinitionReader(String scanPackage) {

        this.scanPackage = scanPackage;

    }


    public List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> result = new ArrayList<>();
        for (Class<?> beanClass : ScanExecutor.getInstance().search(scanPackage)) {
            if (beanClass.isInterface()) {
                continue;
            }
            result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

            //如果在DI时字段类型是接口，那么我们读取它实现类的配置
            for (Class<?> i : beanClass.getInterfaces()) {
                result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
            }

        }

        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }


}
