package com.ruoyi.web.controller.system;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysMenuService;

/**
 * 登录验证 控制器
 * 处理用户的登录操作、获取当前登录用户信息及前端动态路由菜单等核心认证请求
 * 
 * @author ruoyi
 */
@RestController // 声明这是一个RESTful控制器
public class SysLoginController
{
    /** 登录验证相关的服务类，包含密码校验、生成Token、记录登录日志等核心逻辑 */
    @Autowired
    private SysLoginService loginService;

    /** 菜单服务接口，用于获取用户的路由及菜单结构 */
    @Autowired
    private ISysMenuService menuService;

    /** 权限服务类，主要用于获取用户具有的角色标识列表及菜单权限标识列表 */
    @Autowired
    private SysPermissionService permissionService;

    /** Token服务类，用于管理Redis中的Token信息，刷新过期时间等 */
    @Autowired
    private TokenService tokenService;

    /** 参数配置服务类，用于读取系统配置项（如密码过期策略等） */
    @Autowired
    private ISysConfigService configService;

    /**
     * 登录方法
     * 处理前端提交的用户账号密码和验证码，校验成功后返回 JWT Token
     * 
     * @param loginBody 包含登录信息(用户名、密码、验证码code、验证码唯一标识uuid)的实体对象
     * @return 包含生成的 Token 字符串的结果对象
     */
    @PostMapping("/login") // 处理POST请求，映射 "/login"
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        // 1. 初始化一个表示成功的Ajax结果对象
        AjaxResult ajax = AjaxResult.success();
        // 2. 调用登录服务的login方法。如果校验失败（如密码错误或验证码错误），该方法内部会抛出异常被全局异常处理器捕获
        //    如果校验成功，则返回一个生成的 JWT Token
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        // 3. 将 Token 放入结果对象的 "token" 字段中返回给前端（前端会将其存储在 Cookie 或 LocalStorage 中）
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 用户登录成功后，前端会调用此接口获取当前用户的详细信息、拥有的角色集合和权限集合
     * 
     * @return 包含用户信息、角色列表、权限列表等状态属性的结果对象
     */
    @GetMapping("getInfo") // 处理GET请求
    public AjaxResult getInfo()
    {
        // 1. 从Spring Security的上下文中获取当前已经认证过的用户登录模型对象
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 2. 提取出具体的数据库用户实体对象
        SysUser user = loginUser.getUser();
        // 3. 调用权限服务，获取该用户所属的角色标识字符串集合（如 ["admin", "common"]）
        Set<String> roles = permissionService.getRolePermission(user);
        // 4. 调用权限服务，获取该用户拥有的菜单按钮权限标识字符串集合（如 ["system:user:add", "system:role:list"]）
        Set<String> permissions = permissionService.getMenuPermission(user);
        
        // 5. 判断如果Redis/Token中缓存的权限与实时查询的权限不一致（即可能刚被管理员修改了权限）
        if (!loginUser.getPermissions().equals(permissions))
        {
            // 更新缓存中的权限集合
            loginUser.setPermissions(permissions);
            // 刷新Token状态并同步到Redis
            tokenService.refreshToken(loginUser);
        }
        
        // 6. 构造成功的返回结果
        AjaxResult ajax = AjaxResult.success();
        // 放入基本用户信息
        ajax.put("user", user);
        // 放入角色标识集合
        ajax.put("roles", roles);
        // 放入权限标识集合
        ajax.put("permissions", permissions);
        // 检查并放入：是否需要提示修改初始密码（根据配置和上次密码修改时间判断）
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        // 检查并放入：密码是否已经过期，需要强制修改
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    /**
     * 获取路由信息
     * 登录成功后，前端根据此接口返回的路由结构渲染左侧的动态菜单栏
     * 
     * @return 包含该用户有权访问的树形路由菜单结构集合
     */
    @GetMapping("getRouters") // 处理GET请求
    public AjaxResult getRouters()
    {
        // 1. 获取当前登录用户的ID
        Long userId = SecurityUtils.getUserId();
        // 2. 根据用户ID查询出该用户拥有的所有目录、菜单类型的列表数据
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        // 3. 将扁平化的菜单列表转换为前端Vue Router需要的树形路由组件配置结构(List<RouterVo>)，并返回成功结果
        return AjaxResult.success(menuService.buildMenus(menus));
    }
    
    /**
     * 检查是否配置了初始密码需要被修改
     * 
     * @param pwdUpdateDate 用户密码的最后更新时间
     * @return 布尔值，true代表需要提示修改，false代表不需要
     */
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        // 从系统参数配置中读取对应键的值，并转化为整型（1表示开启提醒）
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        // 如果配置存在且值为1，并且用户的密码修改时间为空（说明使用的是初始密码，从未改过），则返回true
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    /**
     * 检查密码是否已经过期
     * 
     * @param pwdUpdateDate 用户密码的最后更新时间
     * @return 布尔值，true代表已过期，false代表未过期
     */
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        // 从系统参数配置中读取密码的有效天数
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        // 如果系统配置了有效天数且大于0，则进行校验
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            // 如果用户密码更新时间为空，说明从未改过，直接判定为过期，需强制修改
            if (StringUtils.isNull(pwdUpdateDate))
            {
                return true;
            }
            // 获取当前服务器时间
            Date nowDate = DateUtils.getNowDate();
            // 计算当前时间与上次更新时间相差的天数，如果大于配置的有效天数，则说明已过期
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        // 如果未配置或配置小于等于0，则认为密码永不过期
        return false;
    }
}
