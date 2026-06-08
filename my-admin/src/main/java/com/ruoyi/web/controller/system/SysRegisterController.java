package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.SysRegisterService;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 注册验证 控制器
 * 负责处理系统中新用户的公开注册功能（如在登录页面点击注册账号）
 * 
 * @author ruoyi
 */
@RestController // 声明为响应JSON数据的RESTful控制器
public class SysRegisterController extends BaseController
{
    /** 用户注册相关业务逻辑的核心服务类，处理账号的入库及密码加密等操作 */
    @Autowired // 自动装配服务
    private SysRegisterService registerService;

    /** 参数配置服务，用于获取系统动态配置的数据字典或业务开关 */
    @Autowired // 自动装配参数配置服务
    private ISysConfigService configService;

    /**
     * 用户注册接口
     * 接收前端提交的注册信息并在校验通过后持久化到数据库中
     * 
     * @param user 包含注册信息的请求体对象（账号、密码、验证码等）
     * @return 注册操作的结果状态及提示信息
     */
    @PostMapping("/register") // 映射 POST 请求到 "/register" 路径
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        // 1. 调用配置服务，从数据库或缓存中读取名为 "sys.account.registerUser" 的开关配置
        //    如果开关的值不是 "true" (区分大小写，这里必须小写true)，说明当前系统管理员关闭了前台自由注册的功能
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            // 返回带明确错误提示的结果
            return error("当前系统没有开启注册功能！");
        }
        
        // 2. 调用注册服务的 register 方法执行核心注册逻辑
        //    方法会进行验证码校验、账号是否已存在校验、数据入库等，并返回失败的提示消息（成功则返回空字符串）
        String msg = registerService.register(user);
        
        // 3. 判断返回的消息是否为空：如果为空，说明没有出现任何错误，注册成功；如果不为空，则将错误消息返回给前端
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
