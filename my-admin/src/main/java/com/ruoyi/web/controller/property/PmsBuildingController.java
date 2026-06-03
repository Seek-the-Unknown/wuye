package com.ruoyi.web.controller.property;

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
import com.ruoyi.property.domain.PmsBuilding;
import com.ruoyi.property.service.IPmsBuildingService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/building")
public class PmsBuildingController extends BaseController
{
    @Autowired
    private IPmsBuildingService pmsBuildingService;

    @PreAuthorize("@ss.hasPermi('property:building:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsBuilding pmsBuilding)
    {
        startPage();
        List<PmsBuilding> list = pmsBuildingService.selectPmsBuildingList(pmsBuilding);
        return getDataTable(list);
    }

    /** 获取全部楼宇（不分页，供下拉选用） */
    @GetMapping("/listAll")
    public AjaxResult listAll(PmsBuilding pmsBuilding)
    {
        return success(pmsBuildingService.selectPmsBuildingList(pmsBuilding));
    }

    @PreAuthorize("@ss.hasPermi('property:building:query')")
    @GetMapping(value = "/{buildingId}")
    public AjaxResult getInfo(@PathVariable("buildingId") Long buildingId)
    {
        return success(pmsBuildingService.selectPmsBuildingByBuildingId(buildingId));
    }

    @PreAuthorize("@ss.hasPermi('property:building:add')")
    @Log(title = "楼宇管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsBuilding pmsBuilding)
    {
        pmsBuilding.setCreateBy(getUsername());
        return toAjax(pmsBuildingService.insertPmsBuilding(pmsBuilding));
    }

    @PreAuthorize("@ss.hasPermi('property:building:edit')")
    @Log(title = "楼宇管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsBuilding pmsBuilding)
    {
        pmsBuilding.setUpdateBy(getUsername());
        return toAjax(pmsBuildingService.updatePmsBuilding(pmsBuilding));
    }

    @PreAuthorize("@ss.hasPermi('property:building:remove')")
    @Log(title = "楼宇管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{buildingIds}")
    public AjaxResult remove(@PathVariable Long[] buildingIds)
    {
        return toAjax(pmsBuildingService.deletePmsBuildingByBuildingIds(buildingIds));
    }
}
