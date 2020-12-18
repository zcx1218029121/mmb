package com.mmb.beans.support;

import com.mmb.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author loafer
 */
public class DaoDefinitionReader {
    private final List<String> registryBeanClasses = new ArrayList<String>();
    private ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    public DaoDefinitionReader(String scanPackage, Class<?> runClass) {

        URL url = runClass.getResource("");
        String protocol = url.getProtocol();
        switch (protocol) {
            case "file":
                doScanner(scanPackage);
                break;
            case "jar":
                doScannerJar(scanPackage);
                break;
            default:
                throw new RuntimeException("err");
        }

    }

    public List<String> getRegistryBeanClasses() {
        return registryBeanClasses;
    }

    private void doScanner(String scanPackage) {
        ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();
        URL url = classLoaderWrapper.getResourceAsURL(scanPackage.replaceAll("\\.", "/"));
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


    private void doScannerJar(final String packName) {
        String pathName = packName.replace(".", "/");
        JarFile jarFile = null;
        try {
            URL url = classLoaderWrapper.getResourceAsURL(pathName);
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if (jarEntryName.contains(pathName) && !jarEntryName.equals(pathName + "/")) {
                //递归遍历子目录
                if (jarEntry.isDirectory()) {
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                        doScannerJar(prefix);
                    }
                } else if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName().replace("/", ".").replace(".class", "");
                    registryBeanClasses.add(className);
                }
            }

        }

    }

}
