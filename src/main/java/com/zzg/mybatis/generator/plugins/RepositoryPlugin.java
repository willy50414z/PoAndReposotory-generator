package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: mybatis-generator-gui
 *
 * @author slankka on 2017/12/13.
 */
public class RepositoryPlugin extends PluginAdapter {

	private FullyQualifiedJavaType annotationRepository;
	private String annotation = "@Repository";// 設定引入annotation

	public RepositoryPlugin() {
		// 設定引入包名
		annotationRepository = new FullyQualifiedJavaType("org.springframework.stereotype.Repository"); //$NON-NLS-1$
	}

	@Override
	public boolean validate(List<String> list) {
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		//Spring JPA不套用Mybatis預設方法
		try {
			java.lang.reflect.Field f = Interface.class.getSuperclass().getDeclaredField("methods");
			f.setAccessible(true);
			f.set(interfaze, new ArrayList<Method>());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		interfaze.addImportedType(annotationRepository);
		interfaze.addAnnotation(annotation);
		return true;
	}
}
