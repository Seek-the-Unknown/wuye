package com.ruoyi.web.controller.property;

import java.util.Date;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.property.domain.PmsRepair;
import com.ruoyi.property.service.IPmsRepairService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 物业报修管理控制器
 *
 * 负责处理业主的报修申请、物业指派维修工、查询维修人员名单等功能。
 */
@RestController
@RequestMapping("/property/repair")
public class PmsRepairController extends BaseController
{
    @Autowired
    private IPmsRepairService pmsRepairService;

    /** 注入系统用户服务，主要用于查询维修人员角色列表 */
    @Autowired
    private ISysUserService userService;

    /**
     * 分页查询所有的报修工单列表
     * @param pmsRepair 包含报修状态、业主姓名等查询条件的对象
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('property:repair:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsRepair pmsRepair)
    {
        // 1. 开启分页拦截器
        startPage();
        // 2. 查询出符合条件的报修记录集合
        List<PmsRepair> list = pmsRepairService.selectPmsRepairList(pmsRepair);
        // 3. 打包成分页格式返回
        return getDataTable(list);
    }

    /**
     * 获取单一报修工单的详细信息（用于查看详情或编辑时的回显）
     * @param repairId 报修单ID
     * @return 包含工单信息的标准响应
     */
    @PreAuthorize("@ss.hasPermi('property:repair:query')")
    @GetMapping(value = "/{repairId}")
    public AjaxResult getInfo(@PathVariable("repairId") Long repairId)
    {
        return success(pmsRepairService.selectPmsRepairByRepairId(repairId));
    }

    /**
     * 业主/管理员新增报修申请
     * @param pmsRepair 前端提交的报修表单数据
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('property:repair:add')")
    @Log(title = "报修管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsRepair pmsRepair)
    {
        // 标记记录的创建人为当前登录用户
        pmsRepair.setCreateBy(getUsername());
        return toAjax(pmsRepairService.insertPmsRepair(pmsRepair));
    }

    /**
     * 修改/编辑已存在的报修工单
     * @param pmsRepair 修改后的数据
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('property:repair:edit')")
    @Log(title = "报修管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsRepair pmsRepair)
    {
        // 标记最后一次更新人为当前登录用户
        pmsRepair.setUpdateBy(getUsername());
        return toAjax(pmsRepairService.updatePmsRepair(pmsRepair));
    }

    /**
     * 核心业务：物业派单功能（指派给特定维修工）
     * @param pmsRepair 包含需指派工单ID和被指派人ID的对象
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('property:repair:edit')")
    @Log(title = "报修指派", businessType = BusinessType.UPDATE)
    @PutMapping("/assign")
    public AjaxResult assign(@RequestBody PmsRepair pmsRepair)
    {
        // 1. 记录系统分配维修人员的时刻
        pmsRepair.setAssignTime(new Date());
        // 2. 将工单的状态流转为 "1"(处理中)
        pmsRepair.setRepairStatus("1");
        // 3. 记录操作人
        pmsRepair.setUpdateBy(getUsername());
        // 4. 将更改刷入数据库
        return toAjax(pmsRepairService.updatePmsRepair(pmsRepair));
    }

    /**
     * 业务辅助：获取可以用来派单的所有内部维修工名单
     * @return 包含维修工用户列表的 AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('property:repair:edit')")
    @GetMapping("/workers")
    public AjaxResult getWorkers()
    {
        // 1. 初始化用户查询对象
        SysUser query = new SysUser();
        // 2. 这里的 roleId = 108 是在系统中固定配置的"维修工"角色的ID
        // 只有拥有这个角色权限的员工才会被列入下拉候选名单
        query.setRoleId(108L);
        // 3. 查询并返回所有具备该角色的用户列表
        return success(userService.selectAllocatedList(query));
    }

    /**
     * 批量或单条删除作废的报修单
     * @param repairIds 需要删除的主键ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('property:repair:remove')")
    @Log(title = "报修管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{repairIds}")
    public AjaxResult remove(@PathVariable Long[] repairIds)
    {
        return toAjax(pmsRepairService.deletePmsRepairByRepairIds(repairIds));
    }
}
