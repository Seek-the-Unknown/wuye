package com.ruoyi.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 个人信息 业务处理 控制器
 * 处理当前登录用户的基本信息查询、信息修改、密码重置、头像上传等操作
 * 
 * @author ruoyi
 */
@RestController // 声明为响应JSON的控制器组件
@RequestMapping("/system/user/profile") // 设置全局路由基础路径
public class SysProfileController extends BaseController
{
    /** 用户服务接口，执行与用户信息相关的数据库操作和业务校验 */
    @Autowired // 自动装配服务类
    private ISysUserService userService;

    /** Token服务接口，处理登录用户的身份信息、权限验证信息在 Redis 中的管理及刷新 */
    @Autowired // 自动装配Token处理类
    private TokenService tokenService;

    /**
     * 查询个人主页的基本信息
     * 
     * @return 包含当前用户信息、所属角色名称集合、所属岗位名称集合的结果对象
     */
    @GetMapping // 处理 GET 请求
    public AjaxResult profile()
    {
        // 1. 获取当前安全上下文中的登录用户对象
        LoginUser loginUser = getLoginUser();
        // 2. 从上下文中取出用户实体对象
        SysUser user = loginUser.getUser();
        // 3. 构建成功的响应体，将主体设为该用户信息
        AjaxResult ajax = AjaxResult.success(user);
        // 4. 调用服务查询出该用户拥有的角色名称组（多个角色会用逗号拼成字符串），放入响应中
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        // 5. 调用服务查询出该用户拥有的岗位名称组，并放入响应中
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        // 6. 返回组装完毕的个人信息及扩展属性集合
        return ajax;
    }

    /**
     * 修改保存个人信息
     * 
     * @param user 前端传入的包含修改后的用户昵称、手机、邮箱、性别等信息的实体对象
     * @return 修改操作的结果状态
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE) // 记录日志：修改动作
    @PutMapping // 处理 PUT 请求
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        // 1. 从当前上下文中获取登录用户以及它的原始实体数据
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 2. 使用前端传来的数据覆盖原对象里的各个基础字段
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        
        // 3. 如果修改了手机号，需要校验这个新的手机号是否与其他系统用户产生冲突
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        // 4. 如果修改了邮箱，校验邮箱地址的全局唯一性
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        
        // 5. 调用服务层真正更新到数据库，如果影响的行数大于 0 则表示成功
        if (userService.updateUserProfile(currentUser) > 0)
        {
            // 6. 成功修改后，实时更新缓存里的用户基本信息对象（防止后续调用报错或信息不一致）
            tokenService.setLoginUser(loginUser);
            // 7. 返回成功标识
            return success();
        }
        // 若数据库未有任何记录变更，返回错误信息
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置个人密码
     * 
     * @param params 包含前端输入的旧密码和新密码的Map集合
     * @return 修改密码的结果状态
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE) // 记录日志：更新动作
    @PutMapping("/updatePwd") // 处理 PUT 请求，路径为 "/updatePwd"
    public AjaxResult updatePwd(@RequestBody Map<String, String> params)
    {
        // 1. 提取入参中的旧密码与新密码
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        // 2. 获取当前登录用户的模型与ID
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        // 3. 再次查询数据库最新的用户实体记录以获得正确的加密密码串
        SysUser user = userService.selectUserById(userId);
        String password = user.getPassword();
        
        // 4. 对比明文旧密码与库中密文是否吻合
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        // 5. 校验新密码是否与旧密码一模一样
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error("新密码不能与旧密码相同");
        }
        
        // 6. 采用Security提供的算法对新明文密码进行加密存储
        newPassword = SecurityUtils.encryptPassword(newPassword);
        // 7. 调用业务方法将新密码持久化到数据库中
        if (userService.resetUserPwd(userId, newPassword) > 0)
        {
            // 8. 成功后，需要同时更新内存模型中的密码和密码最后更新时间属性
            loginUser.getUser().setPwdUpdateDate(DateUtils.getNowDate());
            loginUser.getUser().setPassword(newPassword);
            // 9. 更新 Redis 中的缓存 token 信息
            tokenService.setLoginUser(loginUser);
            // 10. 返回成功响应
            return success();
        }
        // 出现异常无更新则返回提示
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     * 处理当前用户上传新头像文件的请求，并删除服务器上的旧文件
     * 
     * @param file 通过表单传递上来的头像图片文件数据流
     * @return 包含最新头像资源路径(imgUrl)的响应结果
     * @throws Exception 上传异常或文件读取异常
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE) // 记录系统日志
    @PostMapping("/avatar") // 处理 POST 请求
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        // 1. 校验前端是否传递了有效文件
        if (!file.isEmpty())
        {
            // 2. 获取当前系统登录的用户信息
            LoginUser loginUser = getLoginUser();
            // 3. 调用公共文件上传工具上传头像到默认头像目录，并限制扩展名为图片类型
            //    返回值是相对业务的路径（例如 /profile/avatar/xxx.jpg）
            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);
            
            // 4. 将新头像的路径更新到数据库用户的 avatar 字段中
            if (userService.updateUserAvatar(loginUser.getUserId(), avatar))
            {
                // 5. 提取用户修改前对应的旧头像路径
                String oldAvatar = loginUser.getUser().getAvatar();
                // 6. 如果旧头像路径不为空，则应该将服务器上的旧文件删除，节省空间
                if (StringUtils.isNotEmpty(oldAvatar))
                {
                    // 将外部可访问的路径去掉前缀后，拼接到实际本地磁盘存储路径上并删除该物理文件
                    FileUtils.deleteFile(RuoYiConfig.getProfile() + FileUtils.stripPrefix(oldAvatar));
                }
                
                // 7. 将成功响应对象中封装前端所需的图片URL参数
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatar);
                
                // 8. 更新当前登录用户的内存及缓存模型中的头像属性
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                // 9. 正常返回响应
                return ajax;
            }
        }
        // 若没有文件或更新失败，则提示异常信息
        return error("上传图片异常，请联系管理员");
    }
}
