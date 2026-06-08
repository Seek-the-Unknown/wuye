package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;

/**
 * 首页 控制器
 * 处理默认访问根路径 ("/") 的请求，通常用于测试后端应用是否正常启动
 *
 * @author ruoyi
 */
@RestController // 声明这是一个RESTful控制器，直接返回字符串等数据到客户端
public class SysIndexController
{
    /** 系统基础配置实体，封装了application.yml中的自定义配置属性（如项目名称、版本号等） */
    @Autowired // 自动注入配置对象
    private RuoYiConfig ruoyiConfig;

    /**
     * 访问首页，返回提示语
     * 当浏览器直接访问后端接口基础地址时触发此方法
     * 
     * @return 包含项目名称和版本号的欢迎字符串
     */
    @RequestMapping("/") // 映射根路径请求
    public String index()
    {
        // 使用StringUtils的format方法，将占位符 "{}" 依次替换为配置文件中读取的项目名称和版本号，返回友好的文本提示
        return StringUtils.format("欢迎使用{}，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }
}
