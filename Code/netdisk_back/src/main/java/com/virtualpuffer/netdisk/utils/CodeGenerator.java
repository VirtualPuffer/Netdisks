package com.virtualpuffer.netdisk.utils;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrDoubleZhang
 * @ClassName CodeGenerator
 * @Date 2020/12/14 3:13 PM
 * @Version 1.0
 **/
public class CodeGenerator {

    public static void main(String[] args) {

        // 配置项一览
        String auth = "MrDoubleZhang"; // 作成者姓名
        String url = "jdbc:mysql://47.96.253.99:3306/gangballs?characterEncoding=utf-8&serverTimezone=UTC&useSSL=false"; // DB链接URL
        String driver = "com.mysql.cj.jdbc.Driver"; // JDBC接口驱动
        String userName = "gangballs"; // DB用户名
        String password = "Where.gangballs123"; // DB用户密码
        String parentPackage = "com.unbelievable"; // 父类目录
        String childPackage = "gangballs"; // 子目录（业务目录）
        String[] tables = {}; // 表名 （用于设置include表单） 表后期添加需要使用

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // set freemarker engine
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor(auth);
        gc.setOpen(false);
//        gc.setMapperName("%sDao");
//        gc.setServiceName("%sService");

        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        // dsc.setSchemaName("public");
        dsc.setDriverName(driver);
        dsc.setUsername(userName);
        dsc.setPassword(password);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(childPackage);
        pc.setParent(parentPackage);
//        pc.setMapper("dao");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/generator/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/mysql/" + tableInfo.getEntityName() + "Mapper.xml";
//                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
//                        + "/" + tableInfo.getEntityName() + "Mapper.xml";
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("templates/generator/entity.java");
        templateConfig.setService("templates/generator/service.java");
        templateConfig.setServiceImpl("templates/generator/serviceImpl.java");
        templateConfig.setController("templates/generator/controller.java");
        templateConfig.setMapper("templates/generator/mapper.java");


        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(false);
        // 公共父类
//        strategy.setSuperControllerClass("");
        // 写于父类中的公共字段
        strategy.setSuperEntityColumns("id");
        if (tables.length>0) {
            strategy.setInclude(tables);
        } else {
            strategy.setExclude();
        }
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

}
