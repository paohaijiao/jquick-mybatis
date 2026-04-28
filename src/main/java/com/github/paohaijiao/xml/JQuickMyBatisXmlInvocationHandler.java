package com.github.paohaijiao.xml;

import com.github.paohaijiao.anno.JPageParam;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.model.JPage;
import com.github.paohaijiao.model.JPageParams;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.statement.JQuickStatementResultMapper;
import com.github.paohaijiao.type.JTypeReference;
import com.github.paohaijiao.util.JPageUtil;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class JQuickMyBatisXmlInvocationHandler extends JQuickXmlInvocationHandler {

    private Connection connection;


    public JQuickMyBatisXmlInvocationHandler() {
    }

    public JQuickMyBatisXmlInvocationHandler(Connection connection) {
        this.connection = connection;
    }

    public void setDataSource(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected Object loadResult(String sql, JContext jContext, Method method,Object[] args) {
        JAssert.notNull(connection, "Connection is null");
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            Class<?> returnType = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();
            if (returnType.equals(Void.TYPE) || returnType.equals(java.lang.Void.class)) {
                return null;
            }
            if(isPageType(genericReturnType)) {//pagination
                JPageParams pageParams = extractPageParams(method, jContext,args);
                Type elementType = getPageElementType(genericReturnType);
                JTypeReference<List<?>> listTypeRef = createListTypeReference(elementType);
                List<?> allData = (List<?>) JQuickStatementResultMapper.mapResultSet(rs, listTypeRef);
                return JPageUtil.page(allData, pageParams.getPageNum(), pageParams.getPageSize());
            }else{
                JTypeReference<?> typeRef = createTypeReference(genericReturnType);
                return JQuickStatementResultMapper.mapResultSet(rs, typeRef);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean isPageType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type rawType = pt.getRawType();
            if (rawType instanceof Class) {
                return JPage.class.isAssignableFrom((Class<?>) rawType);
            }
        }
        return false;
    }
    private JTypeReference<List<?>> createListTypeReference(Type elementType) {
        return new JTypeReference<List<?>>() {
            @Override
            public Type getType() {
                return new ParameterizedType() {
                    @Override
                    public Type[] getActualTypeArguments() {
                        return new Type[]{elementType};
                    }
                    @Override
                    public Type getRawType() {
                        return List.class;
                    }
                    @Override
                    public Type getOwnerType() {
                        return null;
                    }
                };
            }
        };
    }
    private Type getPageElementType(Type pageType) {
        if (pageType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) pageType;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                return actualTypeArguments[0];
            }
        }
        return null;
    }
    /**
     * 提取分页参数（核心方法）
     * 优先级：方法参数上的 @JPageParam 注解 > JContext 中的参数 > 默认值
     *
     * @param method   目标方法
     * @param args 上下文参数
     * @return 分页参数
     */
    private JPageParams extractPageParams(Method method, JContext jContext,Object[] args) {
        Integer pageNum = null;
        Integer pageSize = null;
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            JPageParam jPageParam = parameters[i].getAnnotation(JPageParam.class);
            if (jPageParam != null) {
                String paramName = jPageParam.value();
                Object value = args[i];
                if ("pageNum".equals(paramName)) {
                    pageNum = (Integer) value;
                } else if ("pageSize".equals(paramName)) {
                    pageSize = (Integer) value;
                }
            }
        }
        JAssert.notNull(pageNum, "pageNum require not  null");
        JAssert.notNull(pageNum, "pageSize require not null");
        return new JPageParams(pageNum, pageSize);
    }
}
