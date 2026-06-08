package com.ruoyi.web.controller.system;

import java.util.List;
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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 部门信息 控制器
 * 处理系统部门（组织架构）的树形结构查询、增删改查等请求
 * 
 * @author ruoyi
 */
@RestController // 标识为Spring MVC的RESTful控制器，方法返回结果直接写入HTTP响应体中(JSON格式)
@RequestMapping("/system/dept") // 映射基础请求路径，处理所有以 "/system/dept" 开头的URL请求
public class SysDeptController extends BaseController
{
    /** 部门服务接口，负责执行与部门相关的核心业务逻辑操作 */
    @Autowired // 由Spring自动注入实现类
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     * 通常用于前端页面加载完整的部门表格或树结构数据
     * 
     * @param dept 包含条件过滤的部门实体（如部门名称、状态等）
     * @return 包含部门数据集合的AjaxResult结果
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')") // 权限校验，需具备查询列表的权限
    @GetMapping("/list") // 响应GET请求，路径为 "/system/dept/list"
    public AjaxResult list(SysDept dept)
    {
        // 调用服务层查询符合条件的部门数据集合
        List<SysDept> depts = deptService.selectDeptList(dept);
        // 返回包含部门集合的成功结果
        return success(depts);
    }

    /**
     * 查询部门列表（排除节点及其所有子节点）
     * 通常用于修改部门时，选择上级部门的下拉树，需要把"自己"及"自己的所有下级"排除，以防止循环嵌套死锁
     * 
     * @param deptId 要排除的当前部门ID
     * @return 过滤后的部门数据集合
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')") // 权限校验，同样需具备列表查询权限
    @GetMapping("/list/exclude/{deptId}") // 响应GET请求，路径为 "/system/dept/list/exclude/{deptId}"
    public AjaxResult excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        // 1. 无条件查询全量的部门列表
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        // 2. 利用Java8集合的removeIf方法，移除不符合条件的部门节点
        //    移除条件：当前节点的ID等于被排除的deptId，或者当前节点的祖级列表(ancestors)中包含被排除的deptId
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        // 3. 返回过滤干净后的部门列表
        return success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     * 用于前端修改页面或查看详情时的单条数据回显
     * 
     * @param deptId URL路径中的部门ID
     * @return 包含单条部门对象详细信息的AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')") // 权限校验，需具备查询详情的权限
    @GetMapping(value = "/{deptId}") // 响应GET请求
    public AjaxResult getInfo(@PathVariable Long deptId)
    {
        // 1. 校验当前登录用户对该部门ID是否拥有数据范围访问权限
        deptService.checkDeptDataScope(deptId);
        // 2. 通过ID查询部门信息并成功返回
        return success(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     * 
     * @param dept 前端提交的部门实体数据JSON，使用@Validated进行基础字段规则校验
     * @return 返回新增成功与否的状态
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')") // 权限校验：新增权限
    @Log(title = "部门管理", businessType = BusinessType.INSERT) // 记录日志：新增
    @PostMapping // 响应POST请求
    public AjaxResult add(@Validated @RequestBody SysDept dept)
    {
        // 1. 调用服务校验同级部门中是否已经存在相同的部门名称
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        // 2. 将当前登录用户名记录为该部门记录的创建人
        dept.setCreateBy(getUsername());
        // 3. 执行部门的新增持久化，并转化为通用响应格式
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     * 
     * @param dept 前端提交的部门修改实体数据，通过@Validated校验
     * @return 包含修改结果的Ajax状态
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')") // 权限校验：编辑权限
    @Log(title = "部门管理", businessType = BusinessType.UPDATE) // 记录日志：更新
    @PutMapping // 响应PUT请求
    public AjaxResult edit(@Validated @RequestBody SysDept dept)
    {
        Long deptId = dept.getDeptId();
        // 1. 再次校验当前用户对目标部门的数据操作权限
        deptService.checkDeptDataScope(deptId);
        
        // 2. 校验同级下新修改的部门名称是否发生重名冲突
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        // 3. 校验逻辑错误：不能将当前部门的上级设为自己，避免造成死循环结构
        else if (dept.getParentId().equals(deptId))
        {
            return error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        // 4. 当要停用(DEPT_DISABLE)该部门时，检查其下面是否还有正常运行(未停用)的子部门。如果存在，则不让停用父级
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0)
        {
            return error("该部门包含未停用的子部门！");
        }
        
        // 5. 将当前登录用户名设为更新人
        dept.setUpdateBy(getUsername());
        // 6. 执行修改操作并返回结果
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     * 
     * @param deptId 路径中的待删除部门ID
     * @return 返回删除的结果信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')") // 权限校验：删除权限
    @Log(title = "部门管理", businessType = BusinessType.DELETE) // 记录日志：删除操作
    @DeleteMapping("/{deptId}") // 响应DELETE请求
    public AjaxResult remove(@PathVariable Long deptId)
    {
        // 1. 检查是否存在下级部门：如果有下级部门，必须先清空或转移下级部门，才能删除当前部门
        if (deptService.hasChildByDeptId(deptId))
        {
            return warn("存在下级部门,不允许删除");
        }
        // 2. 检查部门内是否还有挂载的用户：如果存在用户关联到这个部门，则不允许删除
        if (deptService.checkDeptExistUser(deptId))
        {
            return warn("部门存在用户,不允许删除");
        }
        // 3. 检查当前操作者是否有权限删除该部门（数据权限限制）
        deptService.checkDeptDataScope(deptId);
        // 4. 满足以上安全条件后，执行物理删除并返回操作结果
        return toAjax(deptService.deleteDeptById(deptId));
    }
}
