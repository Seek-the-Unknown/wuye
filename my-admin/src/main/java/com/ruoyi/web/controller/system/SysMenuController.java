package com.ruoyi.web.controller.system;

import java.util.List;
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
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysMenuService;

/**
 * 菜单信息 控制器
 * 处理系统菜单的增删改查、分配角色时的菜单权限树加载等请求
 * 
 * @author ruoyi
 */
@RestController // 声明这是一个RESTful风格的控制器
@RequestMapping("/system/menu") // 映射该控制器的基础请求路径为 "/system/menu"
public class SysMenuController extends BaseController
{
    /** 菜单服务接口，处理与菜单数据相关的业务逻辑 */
    @Autowired // 自动注入对应的服务实现类
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     * 
     * @param menu 包含查询条件的菜单对象（如菜单名称、状态等）
     * @return 包含系统菜单列表数据的AjaxResult结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')") // 权限校验：要求具备菜单列表查询权限
    @GetMapping("/list") // 处理 GET 请求
    public AjaxResult list(SysMenu menu)
    {
        // 调用服务层，根据传入的查询条件和当前登录用户的ID，查询可访问的菜单列表
        // 超级管理员将查出所有菜单，普通用户只查出被分配的菜单
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        // 将查询结果包装为成功的 AjaxResult 对象返回
        return success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     * 用于前端修改页面回显单条菜单记录
     * 
     * @param menuId URL 路径中的菜单ID
     * @return 包含单条菜单详细信息的AjaxResult对象
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')") // 权限校验：要求具备查询权限
    @GetMapping(value = "/{menuId}") // 处理 GET 请求
    public AjaxResult getInfo(@PathVariable Long menuId)
    {
        // 通过ID查询菜单信息，并将其存入成功的结果对象中返回
        return success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     * 通常用于新增或修改菜单时，选择"上级菜单"的下拉树组件数据源
     * 
     * @param menu 菜单查询条件对象
     * @return 包含下拉树结构数据的AjaxResult对象
     */
    @GetMapping("/treeselect") // 处理 GET 请求（此接口由于仅供基础下拉使用，有时不严格拦截权限，或者使用基础角色权限）
    public AjaxResult treeselect(SysMenu menu)
    {
        // 1. 获取当前用户可访问的全量菜单扁平化列表
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        // 2. 将扁平化的菜单列表转换为前端 TreeSelect 组件所识别的具有层级关系（Id、Label、Children）的树结构
        return success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     * 用于角色管理页面，在给某个角色分配权限时，回显该角色已经拥有的菜单树节点
     * 
     * @param roleId 目标角色的ID
     * @return 包含完整菜单树以及被勾选节点ID集合的AjaxResult对象
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}") // 处理 GET 请求
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        // 1. 查询当前操作用户可管理的所有菜单列表（作为整棵树的底板）
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        // 2. 初始化一个成功的返回结果对象
        AjaxResult ajax = AjaxResult.success();
        // 3. 查询指定的角色已经拥有的菜单权限ID列表，放入 "checkedKeys" 字段供前端让对应的复选框打勾
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        // 4. 将底板菜单列表转换为前端所需格式的树结构，放入 "menus" 字段
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        // 5. 返回组装完毕的数据
        return ajax;
    }

    /**
     * 新增菜单
     * 
     * @param menu 前端传递并经过 @Validated 校验过的菜单实体数据
     * @return 包含操作结果状态的AjaxResult对象
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')") // 权限校验：新增权限
    @Log(title = "菜单管理", businessType = BusinessType.INSERT) // 记录系统日志：新增动作
    @PostMapping // 处理 POST 请求
    public AjaxResult add(@Validated @RequestBody SysMenu menu)
    {
        // 1. 校验同级下是否存在相同名称的菜单，防止重名混淆
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        // 2. 如果该菜单被设置为"外链"(即点击新窗口打开)，则它的路由地址必须符合合法的 http/https 协议前缀
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        // 3. 校验路由名称或者路由地址是否与其他已有菜单产生冲突，因为前端Vue-Router不允许相同名字的路由出现
        else if (!menuService.checkRouteConfigUnique(menu))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，路由名称或地址已存在");
        }
        // 4. 将当前登录用户名记录为新菜单的创建者
        menu.setCreateBy(getUsername());
        // 5. 调用服务层将菜单存入数据库，返回插入成功的结果
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     * 
     * @param menu 包含修改信息及菜单ID的实体对象
     * @return 包含操作结果状态的AjaxResult对象
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')") // 权限校验：修改权限
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE) // 记录系统日志：修改动作
    @PutMapping // 处理 PUT 请求
    public AjaxResult edit(@Validated @RequestBody SysMenu menu)
    {
        // 1. 再次校验修改后的同级菜单是否发生重名冲突
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        // 2. 外链检查，必须带 http 前缀
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        // 3. 逻辑安全校验：不能将菜单的父节点设置为它自己，否则会引起树状结构的死循环死锁
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        // 4. 路由唯一性检查，避免同名的路由或组件导致前端页面跳转混乱
        else if (!menuService.checkRouteConfigUnique(menu))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，路由名称或地址已存在");
        }
        // 5. 将当前登录用户标记为更新者
        menu.setUpdateBy(getUsername());
        // 6. 执行数据库记录更新操作并返回执行结果
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     * 
     * @param menuId 待删除菜单的ID
     * @return 包含操作结果状态的AjaxResult对象
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')") // 权限校验：删除权限
    @Log(title = "菜单管理", businessType = BusinessType.DELETE) // 记录系统日志：删除动作
    @DeleteMapping("/{menuId}") // 处理 DELETE 请求
    public AjaxResult remove(@PathVariable("menuId") Long menuId)
    {
        // 1. 检查是否存在子菜单，如果有的话不能直接删除，必须先清空子节点
        if (menuService.hasChildByMenuId(menuId))
        {
            return warn("存在子菜单,不允许删除");
        }
        // 2. 检查此菜单是否已经被分配给任何角色，如果已被关联，需要先解除关联关系才能删除，保证数据完整性
        if (menuService.checkMenuExistRole(menuId))
        {
            return warn("菜单已分配,不允许删除");
        }
        // 3. 满足安全删除条件后，调用服务层执行物理删除
        return toAjax(menuService.deleteMenuById(menuId));
    }
}