package com.mmb.beans.support;

import com.mmb.beans.config.BeanDefinition;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author loafer
 */
public class DaoDefinitionReader {
    private List<String> registryBeanClasses = new ArrayList<String>();
    private String scanPackage;

    public DaoDefinitionReader(String scanPackage) {
        this.scanPackage = scanPackage;
        doScanner(scanPackage);
    }

    public List<String> getRegistryBeanClasses() {
        return registryBeanClasses;
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader()
                .getResource("./" + scanPackage.replaceAll("\\.", "/"));
        assert url != null;
        File classPath = new File(url.getFile());

        for (File file : Objects.requireNonNull(classPath.listFiles())) {

            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registryBeanClasses.add(className);
            }

        }

    }

    public List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registryBeanClasses) {
                Class<?> beanClass = Class.forName(className);

                if (beanClass.isInterface()) {
                    continue;
                }

                //默认是类名首字母小写
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                //如果在DI时字段类型是接口，那么我们读取它实现类的配置
                for (Class<?> i : beanClass.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static void main(String[] args) {
        DaoDefinitionReader daoDefinitionReader = new DaoDefinitionReader("com.mmb.test");
        daoDefinitionReader.loadBeanDefinitions();
    }
}
