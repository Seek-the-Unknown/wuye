package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 角色信息 控制器
 * 处理系统角色的增删改查、数据权限分配、授权用户等业务请求
 * 
 * @author ruoyi
 */
@RestController // 声明这是处理 RESTful 风格请求的控制器，所有返回值以 JSON 形式写回
@RequestMapping("/system/role") // 指定该类中所有方法的请求基础路径
public class SysRoleController extends BaseController
{
    /** 角色服务接口，包含角色数据的核心增删改查逻辑 */
    @Autowired // 自动装配
    private ISysRoleService roleService;

    /** Token 服务，用于操作或刷新登录用户的安全凭证信息 */
    @Autowired
    private TokenService tokenService;

    /** 权限服务接口，用于计算和获取用户的最新菜单权限或数据权限集合 */
    @Autowired
    private SysPermissionService permissionService;

    /** 用户服务接口，处理与用户分配等相关的业务逻辑 */
    @Autowired
    private ISysUserService userService;

    /** 部门服务接口，用于为角色的数据权限(数据范围)提供部门树形结构 */
    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取角色列表
     * 支持分页及条件检索
     * 
     * @param role 包含角色查询条件的对象
     * @return 包含角色的数据列表和总数的分页信息对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')") // 鉴权：需要 'system:role:list' 权限
    @GetMapping("/list") // GET 请求
    public TableDataInfo list(SysRole role)
    {
        // 开启自动分页功能，通过拦截下一个 SQL 查询进行 limit 操作
        startPage();
        // 根据查询条件查询所有的可用角色集合
        List<SysRole> list = roleService.selectRoleList(role);
        // 将普通 List 转化为前端 Table 需要的数据对象返回
        return getDataTable(list);
    }

    /**
     * 导出角色列表到 Excel
     * 
     * @param response HTTP 响应，直接输出生成的文件流
     * @param role 查询条件，控制导出数据的范围
     */
    @Log(title = "角色管理", businessType = BusinessType.EXPORT) // 记录日志：导出动作
    @PreAuthorize("@ss.hasPermi('system:role:export')") // 鉴权：需要导出权限
    @PostMapping("/export") // POST 请求
    public void export(HttpServletResponse response, SysRole role)
    {
        // 1. 无分页的全量条件查询
        List<SysRole> list = roleService.selectRoleList(role);
        // 2. 利用 ExcelUtil 解析 SysRole 类中的 @Excel 注解
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        // 3. 将导出的数据写入工作簿名为 "角色数据" 的文件中供下载
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     * 常用于修改页面或分配权限时回显被选中角色的具体详情
     * 
     * @param roleId 路径参数，角色主键 ID
     * @return 包含角色详细信息的 Ajax 响应结果
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')") // 鉴权：需要详情查询权限
    @GetMapping(value = "/{roleId}") // GET 请求，包含动态参数 {roleId}
    public AjaxResult getInfo(@PathVariable Long roleId)
    {
        // 1. 检查当前用户是否有权限访问该角色（数据范围限制）
        roleService.checkRoleDataScope(roleId);
        // 2. 将查询到的详细实体包裹为成功状态返回
        return success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     * 
     * @param role 角色信息的 JSON 请求体，并用 @Validated 开启属性规则校验
     * @return 包含成功与否标识的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')") // 鉴权：需要新增角色权限
    @Log(title = "角色管理", businessType = BusinessType.INSERT) // 记录日志：新增动作
    @PostMapping // POST 请求
    public AjaxResult add(@Validated @RequestBody SysRole role)
    {
        // 1. 验证整个系统中的角色名称是否发生了冲突
        if (!roleService.checkRoleNameUnique(role))
        {
            return error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        // 2. 验证角色权限标识符(RoleKey)是否全局唯一
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        // 3. 记录新增这条记录的创建人
        role.setCreateBy(getUsername());
        // 4. 调用服务持久化并在成功后通过 toAjax 封装返回
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     * 除了修改基本的名称和标识符，它还可能会更新角色与菜单的关联数据
     * 
     * @param role 带有 ID 和修改后数据的实体对象
     * @return 包含成功与否标识的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')") // 鉴权：需要修改角色权限
    @Log(title = "角色管理", businessType = BusinessType.UPDATE) // 记录日志：更新动作
    @PutMapping // PUT 请求
    public AjaxResult edit(@Validated @RequestBody SysRole role)
    {
        // 1. 校验这个角色是否允许被操作（超级管理员(admin)角色通常不允许被随意修改）
        roleService.checkRoleAllowed(role);
        // 2. 数据访问权限校验，防止越权操作不属于自己的数据
        roleService.checkRoleDataScope(role.getRoleId());
        // 3. 校验修改后的新名称是否产生冲突
        if (!roleService.checkRoleNameUnique(role))
        {
            return error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        // 4. 校验修改后的新角色标识是否产生冲突
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        // 5. 设置记录的最后更新人为当前操作者
        role.setUpdateBy(getUsername());
        
        // 6. 执行修改操作，如果影响数据库记录数大于 0 代表成功
        if (roleService.updateRole(role) > 0)
        {
            // 更新当前登录用户的内存缓存：如果当前修改者本身受到该角色的影响（或是自己改自己角色），则需要立即刷新他在内存中的权限
            LoginUser loginUser = getLoginUser();
            // 如果操作者不是超管（超管无视任何权限且不变），需要重新加载数据库中的角色并刷新权限缓存
            if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin())
            {
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                tokenService.setLoginUser(loginUser);
            }
            return success();
        }
        // 若修改受影响记录数为 0，返回通用错误提示
        return error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限
     * 该接口主要用于当需要专门为某角色配置更细粒度的"数据范围"(如本部门、部门及以下、自定义部门等)时
     * 
     * @param role 包含数据权限标识和所选部门关联信息的实体对象
     * @return 包含成功与否标识的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')") // 鉴权：也属于角色编辑权限的变种
    @Log(title = "角色管理", businessType = BusinessType.UPDATE) // 记录日志：更新动作
    @PutMapping("/dataScope") // PUT 请求
    public AjaxResult dataScope(@RequestBody SysRole role)
    {
        // 1. 防护操作管理员角色
        roleService.checkRoleAllowed(role);
        // 2. 防止越权操作
        roleService.checkRoleDataScope(role.getRoleId());
        // 3. 调用专门授权数据范围的方法（会修改系统角色表中的数据范围字段，并重新维护 sys_role_dept 表的映射）
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     * 处理单独对角色的 "启用/停用" 状态进行切换操作
     * 
     * @param role 只包含 ID 和目标 status 的角色实体
     * @return 状态修改结果
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')") // 鉴权：编辑权限
    @Log(title = "角色管理", businessType = BusinessType.UPDATE) // 记录日志：更新动作
    @PutMapping("/changeStatus") // PUT 请求
    public AjaxResult changeStatus(@RequestBody SysRole role)
    {
        // 1. 依然要防止有人尝试把默认的超管停用
        roleService.checkRoleAllowed(role);
        // 2. 依然要防止越权修改别人的角色状态
        roleService.checkRoleDataScope(role.getRoleId());
        // 3. 记录谁做了这个修改动作
        role.setUpdateBy(getUsername());
        // 4. 调用仅更新状态字段的业务方法
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     * 支持前端传递的一个逗号分隔的 roleIds 进行批量删除
     * 
     * @param roleIds URL路径中的参数数组
     * @return 删除操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')") // 鉴权：删除权限
    @Log(title = "角色管理", businessType = BusinessType.DELETE) // 记录日志：删除动作
    @DeleteMapping("/{roleIds}") // DELETE 请求
    public AjaxResult remove(@PathVariable Long[] roleIds)
    {
        // 调用底层接口，底层会判断角色是否被分配给了用户，如果已被分配则抛出异常阻止删除操作
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     * 提供在分配用户页面时渲染出所有可选角色（不含管理员）
     * 
     * @return 包含可用角色集合对象的 Ajax 结果
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')") // 鉴权：普通查询权限
    @GetMapping("/optionselect") // GET 请求
    public AjaxResult optionselect()
    {
        // 查出所有可用并且用户有权操作的角色用于前端下拉选框
        return success(roleService.selectRoleAll());
    }

    /**
     * 查询已分配给指定角色下的所有用户列表
     * 
     * @param user 包含过滤条件（如账号名、所属角色ID）的用户对象
     * @return 包含这些被授权用户的表格分页数据对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')") // 鉴权：列表访问权限
    @GetMapping("/authUser/allocatedList") // GET 请求
    public TableDataInfo allocatedList(SysUser user)
    {
        // 开启自动分页功能
        startPage();
        // 关联查询含有特定 role_id 的所有用户明细记录
        List<SysUser> list = userService.selectAllocatedList(user);
        // 将结果转为 Table 对象响应
        return getDataTable(list);
    }

    /**
     * 查询尚未分配给指定角色的可用系统用户列表
     * 提供在"添加用户到角色"的挑选弹窗中所用的数据
     * 
     * @param user 包含过滤条件及目标角色ID的用户对象
     * @return 包含所有未被本角色关联的用户的表格分页数据对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')") // 鉴权：同样基于列表查看权限
    @GetMapping("/authUser/unallocatedList") // GET 请求
    public TableDataInfo unallocatedList(SysUser user)
    {
        startPage();
        // 查出未在这个角色下的所有用户
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    /**
     * 取消给单个用户授权某个角色
     * 
     * @param userRole 用户与角色的映射关系实体对象 (需包含 userId 和 roleId)
     * @return 包含取消成功与否标识的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')") // 鉴权：归属为编辑操作
    @Log(title = "角色管理", businessType = BusinessType.GRANT) // 日志：业务类型属于授权相关的操作
    @PutMapping("/authUser/cancel") // PUT 请求
    public AjaxResult cancelAuthUser(@RequestBody SysUserRole userRole)
    {
        // 调用底层，将 sys_user_role 中匹配的一条关系删除
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     * 一次性将指定角色下的多个用户"踢出"该角色
     * 
     * @param roleId 目标角色 ID
     * @param userIds 被取消权限的用户 ID 数组
     * @return 包含批量取消成功与否标识的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll") // PUT 请求
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds)
    {
        // 批量删除关联记录
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     * 一次性将指定角色赋予给多个选中的系统用户
     * 
     * @param roleId 目标角色 ID
     * @param userIds 被授权的用户 ID 数组
     * @return 包含批量授权成功与否标识的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll") // PUT 请求
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds)
    {
        // 1. 数据权限校验
        roleService.checkRoleDataScope(roleId);
        // 2. 执行批量新增操作，往 sys_user_role 中大批量插入关联数据
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }

    /**
     * 获取对应角色的部门树列表
     * 当角色的数据范围需要设为"自定义关联的部门"时，前端下拉树用来展示系统中所有的部门，并自动勾选该角色已经关联的部门
     * 
     * @param roleId 要操作的目标角色 ID
     * @return 包含完整部门树及其勾选节点 ID 集合的数据结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')") // 鉴权：需查询权限
    @GetMapping(value = "/deptTree/{roleId}") // GET 请求，带路径参数
    public AjaxResult deptTree(@PathVariable("roleId") Long roleId)
    {
        // 1. 初始化一个成功响应对象
        AjaxResult ajax = AjaxResult.success();
        // 2. 将当前角色已经绑定的所有部门ID查找出来，放入 "checkedKeys" 字段，供前端自动打勾展示
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        // 3. 将整个系统当前可用的部门层级数据查询出来构成一棵树，放入 "depts" 字段提供基础数据骨架
        ajax.put("depts", deptService.selectDeptTreeList(new SysDept()));
        // 4. 返回完整结构
        return ajax;
    }
}
