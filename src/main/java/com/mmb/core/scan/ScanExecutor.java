package com.mmb.core.scan;

import java.util.Set;
import java.util.function.Predicate;

/**
 * @ProjectName: mbatis
 * @Package: com.mmb.core.scan
 * @ClassName: ScanExecutor
 * @Author: loafer
 * @Description:
 * @Date: 2020/12/23 9:48
 * @Version: 1.0
 */
public class ScanExecutor implements Scan {
    private volatile static ScanExecutor instance;

    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate) {
        Set<Class<?>> fileSearch = null;
        try {
            Scan fileSc = new FileScanner();
            fileSearch = fileSc.search(packageName, predicate);
        } catch (Exception e) {
            // nop
        }
        Scan jarScanner = new JarScanner();
        Set<Class<?>> jarSearch = jarScanner.search(packageName, predicate);
        if (fileSearch == null) {
            return jarSearch;
        } else {
            fileSearch.addAll(jarSearch);
        }

        return fileSearch;
    }

    private ScanExecutor() {
    }

    public static ScanExecutor getInstance() {
        if (instance == null) {
            synchronized (ScanExecutor.class) {
                if (instance == null) {
                    instance = new ScanExecutor();
                }
            }
        }
        return instance;
    }

}
