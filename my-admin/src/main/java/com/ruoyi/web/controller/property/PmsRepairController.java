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

@RestController
@RequestMapping("/property/repair")
public class PmsRepairController extends BaseController
{
    @Autowired
    private IPmsRepairService pmsRepairService;

    @Autowired
    private ISysUserService userService;

    @PreAuthorize("@ss.hasPermi('property:repair:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsRepair pmsRepair)
    {
        startPage();
        List<PmsRepair> list = pmsRepairService.selectPmsRepairList(pmsRepair);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:repair:query')")
    @GetMapping(value = "/{repairId}")
    public AjaxResult getInfo(@PathVariable("repairId") Long repairId)
    {
        return success(pmsRepairService.selectPmsRepairByRepairId(repairId));
    }

    @PreAuthorize("@ss.hasPermi('property:repair:add')")
    @Log(title = "报修管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsRepair pmsRepair)
    {
        pmsRepair.setCreateBy(getUsername());
        return toAjax(pmsRepairService.insertPmsRepair(pmsRepair));
    }

    @PreAuthorize("@ss.hasPermi('property:repair:edit')")
    @Log(title = "报修管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsRepair pmsRepair)
    {
        pmsRepair.setUpdateBy(getUsername());
        return toAjax(pmsRepairService.updatePmsRepair(pmsRepair));
    }

    @PreAuthorize("@ss.hasPermi('property:repair:edit')")
    @Log(title = "报修指派", businessType = BusinessType.UPDATE)
    @PutMapping("/assign")
    public AjaxResult assign(@RequestBody PmsRepair pmsRepair)
    {
        pmsRepair.setAssignTime(new Date());
        pmsRepair.setRepairStatus("1");
        pmsRepair.setUpdateBy(getUsername());
        return toAjax(pmsRepairService.updatePmsRepair(pmsRepair));
    }

    @PreAuthorize("@ss.hasPermi('property:repair:edit')")
    @GetMapping("/workers")
    public AjaxResult getWorkers()
    {
        SysUser query = new SysUser();
        query.setRoleId(108L);
        return success(userService.selectAllocatedList(query));
    }

    @PreAuthorize("@ss.hasPermi('property:repair:remove')")
    @Log(title = "报修管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{repairIds}")
    public AjaxResult remove(@PathVariable Long[] repairIds)
    {
        return toAjax(pmsRepairService.deletePmsRepairByRepairIds(repairIds));
    }
}
