package com.wq.multiuser;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @Description: mybatis自动生成工具
 * @Author: wangqiang01
 * @CreateDate: 2020.5.5
 * @Version: 1.0
 */
public class CodeGenerator {
    /**
     * 表名
     */
    public static final String[] TABLE = new String[]{"user"};
    /**
     * 剔除表名前缀
     */
    public static final String[] TABLE_PRE = new String[]{};
    /**
     * 包名
     */
    private static final String PACKAGE_NAME = "com.wq.multiuser";
    /**
     * 模块名称
     */
    private static final String MODULE_NAME = "domain.entity";
    /**
     * 输出文件的路径
     */
    private static final String OUT_PATH = System.getProperty("user.dir") + "/multi-modules/multi-user/src/main/java";
    /**
     * 代码生成者
     */
    private static final String AUTHOR = "wangqiang01";
    /**
     * JDBC相关配置
     */
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://47.91.250.171:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "130173";

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(OUT_PATH);
        gc.setAuthor(AUTHOR);
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setBaseResultMap(true);
        gc.setEnableCache(true);
        gc.setBaseColumnList(true);
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(URL);
        dsc.setDriverName(DRIVER);
        dsc.setUsername(USER_NAME);
        dsc.setPassword(PASSWORD);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(PACKAGE_NAME);
        pc.setXml("xml");
        pc.setEntity(MODULE_NAME);
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);
        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityColumnConstant(true);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setInclude(TABLE);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(TABLE_PRE);
        mpg.setStrategy(strategy);
        mpg.execute();
    }

}
