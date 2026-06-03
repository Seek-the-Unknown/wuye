package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 物业管理系统 —— 启动类
 *
 * 课程设计：基于SpringBoot + Vue的智慧小区物业管理系统
 * 作者：XXX
 * 指导老师：XXX
 * 日期：2026年6月
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // 这里原来是热部署配置，注释掉不用
        // System.setProperty("spring.devtools.restart.enabled", "false");

        System.out.println("============================================");
        System.out.println("  智慧物业管理系统 正在启动...");
        System.out.println("  SpringBoot版本: 2.5.15");
        System.out.println("  启动端口: 8080");
        System.out.println("============================================");

        SpringApplication.run(RuoYiApplication.class, args);

        System.out.println("============================================");
        System.out.println("  (♥◠‿◠)ﾉﾞ  智慧物业管理平台启动成功！");
        System.out.println("  后台地址: http://localhost:8080");
        System.out.println("  前端地址: http://localhost:80");
        System.out.println("============================================");
    }
}
