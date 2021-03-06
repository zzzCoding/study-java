package com.yaoyaohao.study.mybatis;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.cglib.proxy.Proxy;

/**
 * 拦截sql执行，统计时间
 * 
 * @author liujianzhu
 * @date 2017年6月20日 上午11:50:38
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
		@Signature(type = StatementHandler.class, method = "update", args = { Statement.class }) })
public class MyPlugin implements Interceptor {

	private Properties properties;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		while(Proxy.isProxyClass(target.getClass())) {
			MetaObject metaObject = SystemMetaObject.forObject(target);
			target = metaObject.getValue("h.target");
		}
		MetaObject metaStatementHandler = SystemMetaObject.forObject(target);
		BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
		System.out.println("执行SQL : "+boundSql.getSql());
		//
		long beginTime = System.currentTimeMillis();
		Object result = invocation.proceed();
		System.out.println("执行时间 : " + (System.currentTimeMillis() - beginTime) + " ms");
		return result;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		System.out.println("------> " + properties.getProperty("dbType"));
		this.properties = properties;
	}

}
