package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 用户信息 控制器
 * 该类用于处理系统用户相关的HTTP请求，包括用户的增删改查、导入导出等功能
 * 
 * @author ruoyi
 */
@RestController // 标识这是一个Spring MVC的RESTful风格的控制器，所有方法的返回值自动序列化为JSON格式
@RequestMapping("/system/user") // 映射HTTP请求的基准路径，该控制器处理的所有请求URL都以 "/system/user" 开头
public class SysUserController extends BaseController
{
    /** 用户信息服务接口，提供用户业务相关的逻辑操作 */
    @Autowired // 自动装配，由Spring框架负责注入实现类
    private ISysUserService userService;

    /** 角色信息服务接口，处理与用户角色相关的操作 */
    @Autowired
    private ISysRoleService roleService;

    /** 部门信息服务接口，处理与用户所在部门相关的操作 */
    @Autowired
    private ISysDeptService deptService;

    /** 岗位信息服务接口，处理与用户关联岗位相关的操作 */
    @Autowired
    private ISysPostService postService;

    /**
     * 获取用户列表
     * 处理分页查询用户列表的请求
     *
     * @param user 用户查询条件（如用户名、手机号、状态等），从请求参数中自动封装到SysUser对象中
     * @return 包含用户列表的表格数据对象
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')") // 权限校验：要求当前操作用户必须具有 'system:user:list' 权限标识
    @GetMapping("/list") // 映射GET请求，完整路径为 "/system/user/list"
    public TableDataInfo list(SysUser user)
    {
        // 开启MyBatis的PageHelper分页功能，它会自动拦截下一次的数据库查询并添加分页参数（从请求中读取pageNum和pageSize）
        startPage();
        // 根据传入的条件，调用服务层查询用户数据列表
        List<SysUser> list = userService.selectUserList(user);
        // 将查询到的用户列表封装成前端Table展示所需的分页数据格式（包含总记录数total和当前页数据rows）并返回
        return getDataTable(list);
    }

    /**
     * 导出用户数据到Excel
     * 
     * @param response HTTP响应对象，用于将生成的Excel文件流写回到客户端供下载
     * @param user 用户查询条件，用于筛选需要导出的数据
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT) // 记录系统操作日志：模块为"用户管理"，业务类型为"导出"
    @PreAuthorize("@ss.hasPermi('system:user:export')") // 权限校验：要求用户具备导出权限
    @PostMapping("/export") // 映射POST请求，完整路径为 "/system/user/export"
    public void export(HttpServletResponse response, SysUser user)
    {
        // 1. 根据前端传入的查询条件参数(user)，从数据库中查询出所有符合条件的用户列表集合
        List<SysUser> list = userService.selectUserList(user);
        // 2. 实例化自定义的Excel工具类，泛型指定为SysUser实体类，用于解析实体类中@Excel注解配置的列信息
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        // 3. 调用工具类的导出方法，将收集到的用户数据(list)写入Excel工作簿中
        //    参数response用于直接响应二进制文件流，参数"用户数据"用于指定工作表(Sheet)的名称
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 导入用户数据
     * 根据上传的Excel文件，批量导入用户数据到系统中
     * 
     * @param file 客户端上传的包含用户数据的Excel文件
     * @param updateSupport 布尔值参数，标识如果导入的用户登录账号已存在，是否支持更新覆盖原有数据
     * @return 导入结果的提示信息（成功和失败的统计情况）
     * @throws Exception 抛出解析文件时可能出现的IO异常或其他未知异常
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT) // 记录系统操作日志：模块为"用户管理"，业务类型为"导入"
    @PreAuthorize("@ss.hasPermi('system:user:import')") // 权限校验：要求用户具备导入权限
    @PostMapping("/importData") // 映射POST请求，完整路径为 "/system/user/importData"
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        // 1. 创建针对SysUser实体类的Excel工具类对象，用于反向解析Excel文件到Java对象的映射
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        // 2. 调用工具类的importExcel方法，读取上传文件(file)的字节输入流
        //    该方法会根据SysUser类上的@Excel注解，将Excel表格中的每一行数据转换为一个SysUser对象，最后返回对象的List集合
        List<SysUser> userList = util.importExcel(file.getInputStream());
        // 3. 获取当前正在执行导入操作的登录用户的用户名，作为被导入用户数据的创建人(createBy)或更新人(updateBy)
        String operName = getUsername();
        // 4. 调用用户服务的业务方法处理导入逻辑，包含合法性校验、新增或更新数据的操作
        //    它会逐条处理并累加成功或失败的信息，最后返回一段描述结果的字符串(message)
        String message = userService.importUser(userList, updateSupport, operName);
        // 5. 封装导入结果信息到成功状态的AjaxResult中，返回给前端进行提示展示
        return success(message);
    }

    /**
     * 下载导入用户数据的Excel模板
     * 
     * @param response HTTP响应对象，用于返回下载的Excel文件流
     */
    @PostMapping("/importTemplate") // 映射POST请求，完整路径为 "/system/user/importTemplate"
    public void importTemplate(HttpServletResponse response)
    {
        // 1. 初始化Excel工具类实例，泛型指定为待导出的SysUser实体类型
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        // 2. 调用工具类的生成模板方法，该方法仅根据SysUser类中带有@Excel注解且支持导入的属性，生成对应的表头
        //    生成的模板文件中不包含任何数据行，只有列名。参数"用户数据"用于指定工作表的名称
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     * 用于在编辑用户时，回显用户的基本信息、分配的岗位、分配的角色等
     * 
     * @param userId 路径参数中的用户ID（非必传，如果是新增用户的回显，则无userId）
     * @return 包含用户详细信息及关联角色、岗位列表的AjaxResult结果集
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')") // 权限校验：查询权限
    @GetMapping(value = { "/", "/{userId}" }) // 支持两个路径映射：无参访问（用于新增弹窗），有参访问（用于修改弹窗）
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        // 1. 初始化一个表示请求成功的AjaxResult对象
        AjaxResult ajax = AjaxResult.success();
        // 2. 判断路径参数中是否传递了用户ID，如果传递了，说明是进行修改用户操作的回显请求
        if (StringUtils.isNotNull(userId))
        {
            // 2.1 校验当前登录用户是否有权限访问这个被查询的用户的业务数据（数据权限过滤）
            userService.checkUserDataScope(userId);
            // 2.2 根据用户ID从数据库查询该用户的详细信息实体
            SysUser sysUser = userService.selectUserById(userId);
            // 2.3 将用户基本信息放入返回结果的 'data' 字段中
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            // 2.4 查询该用户关联的岗位ID列表，存入返回结果的 'postIds' 字段中
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            // 2.5 获取该用户关联的角色对象集合，通过Stream流将其转化为角色ID的List集合，存入 'roleIds' 字段中
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        // 3. 查询系统中所有的可用角色列表
        List<SysRole> roles = roleService.selectRoleAll();
        // 4. 将所有可用角色放入返回结果。如果是管理员操作，则返回所有角色；如果不是管理员，则将超管角色过滤掉，防止普通操作员赋予超管权限
        ajax.put("roles", SecurityUtils.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        // 5. 查询系统中所有的可用岗位列表，放入返回结果中
        ajax.put("posts", postService.selectPostAll());
        // 6. 返回组装完毕的完整信息
        return ajax;
    }

    /**
     * 新增用户
     * 
     * @param user 前端传递的SysUser的JSON数据，经过@Validated校验其参数有效性
     * @return 包含新增操作成功或失败结果的AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')") // 权限校验：新增权限
    @Log(title = "用户管理", businessType = BusinessType.INSERT) // 记录日志：新增操作
    @PostMapping // 映射无具体路径的POST请求（即直接请求 "/system/user"）
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        // 1. 校验当前登录用户对传入的部门ID是否具有数据访问权限
        deptService.checkDeptDataScope(user.getDeptId());
        // 2. 校验当前登录用户对传入的角色ID集合是否具有数据访问权限
        roleService.checkRoleDataScope(user.getRoleIds());
        
        // 3. 校验填写的用户名是否已经存在，如果不唯一则返回错误提示
        if (!userService.checkUserNameUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        // 4. 如果填写了手机号，校验手机号是否已存在
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        // 5. 如果填写了邮箱，校验邮箱地址是否已存在
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        
        // 6. 将当前登录用户名作为新增用户的创建人字段记录
        user.setCreateBy(getUsername());
        // 7. 使用Security工具类提供的加密算法，对用户传入的明文密码进行加密存储
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        // 8. 调用服务层将用户插入数据库，并将影响的行数通过toAjax转换为标准的响应结果
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     * 
     * @param user 前端传入包含更新字段和userId的JSON数据，并通过@Validated进行校验
     * @return 更新结果
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')") // 权限校验：编辑权限
    @Log(title = "用户管理", businessType = BusinessType.UPDATE) // 记录日志：更新操作
    @PutMapping // 映射无具体路径的PUT请求
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        // 1. 检查是否被允许操作：例如超级管理员账号是不允许被普通用户操作的
        userService.checkUserAllowed(user);
        // 2. 检查数据权限：验证当前操作人是否有权限修改此条用户记录
        userService.checkUserDataScope(user.getUserId());
        // 3. 检查当前操作人是否有权限操作传入的新部门
        deptService.checkDeptDataScope(user.getDeptId());
        // 4. 检查当前操作人是否有权限操作传入的新角色集合
        roleService.checkRoleDataScope(user.getRoleIds());
        
        // 5. 校验更新后的用户名、手机号、邮箱是否在系统中产生了冲突
        if (!userService.checkUserNameUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        
        // 6. 记录当前操作人的用户名到更新人字段
        user.setUpdateBy(getUsername());
        // 7. 调用业务方法更新数据库记录
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     * 支持批量删除，即通过传入的逗号分隔的userId数组进行删除
     * 
     * @param userIds 路径中携带的需要被删除的用户ID数组
     * @return 删除结果
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')") // 权限校验：删除权限
    @Log(title = "用户管理", businessType = BusinessType.DELETE) // 记录日志：删除操作
    @DeleteMapping("/{userIds}") // 映射DELETE请求，路径包含用户ID参数列表
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        // 1. 校验待删除的用户ID列表中是否包含了当前登录者自身的ID，防止自己把自己删掉
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error("当前用户不能删除");
        }
        // 2. 调用服务层的批量删除方法，将结果返回
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     * 
     * @param user 包含用户ID和新明文密码的SysUser对象
     * @return 密码重置结果
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')") // 权限校验：重置密码权限
    @Log(title = "用户管理", businessType = BusinessType.UPDATE) // 记录日志：更新操作
    @PutMapping("/resetPwd") // 映射PUT请求
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        // 1. 验证目标用户是否允许被当前操作人修改（防误改超级管理员）
        userService.checkUserAllowed(user);
        // 2. 校验数据权限范围
        userService.checkUserDataScope(user.getUserId());
        // 3. 对前端传入的新密码进行加密处理
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        // 4. 设置更新操作人
        user.setUpdateBy(getUsername());
        // 5. 更新数据库中的密码字段
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改（如停用/启用用户）
     * 
     * @param user 包含用户ID和待修改状态(status)的实体对象
     * @return 状态更新结果
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')") // 权限校验：编辑权限
    @Log(title = "用户管理", businessType = BusinessType.UPDATE) // 记录日志：更新操作
    @PutMapping("/changeStatus") // 映射PUT请求
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        // 1. 检查是否允许操作目标用户
        userService.checkUserAllowed(user);
        // 2. 检查数据权限范围
        userService.checkUserDataScope(user.getUserId());
        // 3. 记录更新者信息
        user.setUpdateBy(getUsername());
        // 4. 仅更新用户的状态字段
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色列表
     * 该接口常用于对单独用户分配角色的页面回显功能
     * 
     * @param userId 目标用户ID
     * @return 包含该用户详细信息及其拥有的角色的结果集
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')") // 权限校验：查询权限
    @GetMapping("/authRole/{userId}") // 映射GET请求
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        // 1. 查出用户的详细信息
        SysUser user = userService.selectUserById(userId);
        // 2. 查询该用户所属的所有角色列表
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        // 3. 组装响应数据
        ajax.put("user", user);
        // 4. 如果查询者本身就是超级管理员，则能看到所有被查出的角色；如果不是超级管理员，需要把超级管理员角色过滤掉，不能授予他人
        ajax.put("roles", SecurityUtils.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色保存
     * 
     * @param userId 目标用户ID
     * @param roleIds 被授权的角色ID数组
     * @return 操作成功的结果
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')") // 权限校验：编辑权限
    @Log(title = "用户管理", businessType = BusinessType.GRANT) // 记录日志：授权操作
    @PutMapping("/authRole") // 映射PUT请求
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        // 1. 校验对目标用户是否有数据操作权限
        userService.checkUserDataScope(userId);
        // 2. 校验对待赋予的角色是否有数据操作权限
        roleService.checkRoleDataScope(roleIds);
        // 3. 执行角色分配，先清空原有关联，再重新插入新的用户与角色映射关系
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取部门树列表
     * 用于在前端表单、查询条件中展示部门层次结构的下拉树
     * 
     * @param dept 部门查询条件
     * @return 包含树形结构数据的AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')") // 权限校验：需要有用户列表查看权限即可
    @GetMapping("/deptTree") // 映射GET请求
    public AjaxResult deptTree(SysDept dept)
    {
        // 调用部门服务获取全部部门并将其构建成树形结构(List)返回
        return success(deptService.selectDeptTreeList(dept));
    }
}

