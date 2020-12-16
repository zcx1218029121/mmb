package com.mmb.core;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.ParameterizedType;
import java.util.function.Function;

/**
 * 基础的 dbService
 *
 * @author loafer
 */
public abstract class BaseService<T> {
    /**
     * sqlSessionFactory 注入
     */
    private SqlSessionFactory sqlSessionFactory;

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    @SuppressWarnings("all")
    public Class<T> mapperClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    ;


    /**
     * 自动提交
     */
    public <R> R run(Function<T, R> function) {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        T mapper = sqlSession.getMapper(mapperClazz);
        R result;
        try {
            result = function.apply(mapper);
        } finally {
            sqlSession.close();
        }
        return result;
    }

    public SqlSession getSqlSession() {
        return sqlSessionFactory.openSession(true);
    }


    /**
     * 事务包裹
     */
    public <R> R transaction(Function<T, R> function) {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        T mapper = sqlSession.getMapper(mapperClazz);
        R result = null;
        try {
            result = function.apply(mapper);
        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        }
        finally {
            sqlSession.close();
        }
        return result;
    }

}
