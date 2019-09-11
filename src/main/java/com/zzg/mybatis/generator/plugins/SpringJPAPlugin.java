package com.zzg.mybatis.generator.plugins;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansField;

import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import com.zzg.mybatis.generator.util.ConfigHelper;

/**
 * Project: mybatis-generator-gui
 *
 * @author slankka on 2017/12/13.
 */
public class SpringJPAPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }
    //設定interface
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String pkType;
        List<IntrospectedColumn> primaryKeyList = introspectedTable.getPrimaryKeyColumns();
        if(primaryKeyList.size()>1) {
        	//複合主鍵
        	pkType = introspectedTable.getFullyQualifiedTable().getDomainObjectName()+ConfigHelper.pkSuffix;
        }else if(primaryKeyList.size()==1){
        	//單一主鍵
        	pkType = primaryKeyList.get(0).getFullyQualifiedJavaType().toString();
        }else {
        	return true;
        }
        FullyQualifiedJavaType daoSuperType = new FullyQualifiedJavaType("JpaRepository<"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+","+pkType+">");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.data.jpa.repository.JpaRepository"));
        interfaze.addSuperInterface(daoSuperType);
        return true;
    }
    
    //設定PO
    @Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// 該代碼表示在生成class的時候，向topLevelClass添加一個@Setter和@Getter注解
		topLevelClass.addAnnotation("@Entity");
		topLevelClass.addImportedType(new FullyQualifiedJavaType("javax.persistence.Entity"));
		
		
		//在PO層加入PK欄位
		if(introspectedTable.getPrimaryKeyColumns().size()>1) {
			topLevelClass.addAnnotation("@IdClass("+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+ConfigHelper.pkSuffix+".class)");
			topLevelClass.addImportedType(new FullyQualifiedJavaType("javax.persistence.IdClass"));
			topLevelClass.addImportedType(new FullyQualifiedJavaType("javax.persistence.Id"));
			
			List<IntrospectedColumn> pkColList = introspectedTable.getPrimaryKeyColumns();
			for(IntrospectedColumn introspectedColumn : pkColList) {
				Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
				topLevelClass.addField(field);
			}
		}
		
		//PO層不繼承Key類別
		FullyQualifiedJavaType superClass = null;
		topLevelClass.setSuperClass(superClass);
		return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
	}
}
