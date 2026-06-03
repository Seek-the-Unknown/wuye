package com.ruoyi.web.controller.property;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.property.domain.PmsOwner;
import com.ruoyi.property.service.IPmsOwnerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 业主管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/property/owner")
public class PmsOwnerController extends BaseController
{
    @Autowired
    private IPmsOwnerService pmsOwnerService;

    /**
     * 查询业主管理列表
     */
    @PreAuthorize("@ss.hasPermi('property:owner:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsOwner pmsOwner)
    {
        startPage();
        List<PmsOwner> list = pmsOwnerService.selectPmsOwnerList(pmsOwner);
        return getDataTable(list);
    }

    /** 获取全部业主（不分页，供下拉选用） */
    @GetMapping("/listAll")
    public AjaxResult listAll(PmsOwner pmsOwner)
    {
        return success(pmsOwnerService.selectPmsOwnerList(pmsOwner));
    }

    /**
     * 导出业主管理列表
     */
    @PreAuthorize("@ss.hasPermi('property:owner:export')")
    @Log(title = "业主管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PmsOwner pmsOwner)
    {
        List<PmsOwner> list = pmsOwnerService.selectPmsOwnerList(pmsOwner);
        ExcelUtil<PmsOwner> util = new ExcelUtil<PmsOwner>(PmsOwner.class);
        util.exportExcel(response, list, "业主管理数据");
    }

    /**
     * 获取业主管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('property:owner:query')")
    @GetMapping(value = "/{ownerId}")
    public AjaxResult getInfo(@PathVariable("ownerId") Long ownerId)
    {
        return success(pmsOwnerService.selectPmsOwnerByOwnerId(ownerId));
    }

    /**
     * 新增业主管理
     */
    @PreAuthorize("@ss.hasPermi('property:owner:add')")
    @Log(title = "业主管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsOwner pmsOwner)
    {
        pmsOwner.setCreateBy(getUsername());
        return toAjax(pmsOwnerService.insertPmsOwner(pmsOwner));
    }

    /**
     * 修改业主管理
     */
    @PreAuthorize("@ss.hasPermi('property:owner:edit')")
    @Log(title = "业主管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsOwner pmsOwner)
    {
        pmsOwner.setUpdateBy(getUsername());
        return toAjax(pmsOwnerService.updatePmsOwner(pmsOwner));
    }

    /**
     * 删除业主管理
     */
    @PreAuthorize("@ss.hasPermi('property:owner:remove')")
    @Log(title = "业主管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ownerIds}")
    public AjaxResult remove(@PathVariable Long[] ownerIds)
    {
        return toAjax(pmsOwnerService.deletePmsOwnerByOwnerIds(ownerIds));
    }
}
