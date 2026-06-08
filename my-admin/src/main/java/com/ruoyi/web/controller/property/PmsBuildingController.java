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

/**
 * 楼宇管理Controller
 * 
 * 处理楼宇的增删改查等业务请求
 */
@RestController
@RequestMapping("/property/building")
public class PmsBuildingController extends BaseController
{
    /** 楼宇服务接口 */
    @Autowired
    private IPmsBuildingService pmsBuildingService;

    /**
     * 查询楼宇列表
     * 
     * @param pmsBuilding 楼宇信息查询条件
     * @return 分页列表数据
     */
    @PreAuthorize("@ss.hasPermi('property:building:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsBuilding pmsBuilding)
    {
        // 开启分页
        startPage();
        // 根据条件查询楼宇列表
        List<PmsBuilding> list = pmsBuildingService.selectPmsBuildingList(pmsBuilding);
        // 返回分页响应数据
        return getDataTable(list);
    }

    /**
     * 获取全部楼宇（不分页，供下拉选用）
     * 
     * @param pmsBuilding 查询条件
     * @return 包含所有楼宇信息的响应对象
     */
    @GetMapping("/listAll")
    public AjaxResult listAll(PmsBuilding pmsBuilding)
    {
        // 查询所有符合条件的楼宇并返回
        return success(pmsBuildingService.selectPmsBuildingList(pmsBuilding));
    }

    /**
     * 获取楼宇详细信息
     * 
     * @param buildingId 楼宇ID
     * @return 楼宇详情响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:building:query')")
    @GetMapping(value = "/{buildingId}")
    public AjaxResult getInfo(@PathVariable("buildingId") Long buildingId)
    {
        // 根据主键获取详细信息
        return success(pmsBuildingService.selectPmsBuildingByBuildingId(buildingId));
    }

    /**
     * 新增楼宇
     * 
     * @param pmsBuilding 楼宇信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:building:add')")
    @Log(title = "楼宇管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsBuilding pmsBuilding)
    {
        // 设置创建人
        pmsBuilding.setCreateBy(getUsername());
        // 插入记录并返回操作结果
        return toAjax(pmsBuildingService.insertPmsBuilding(pmsBuilding));
    }

    /**
     * 修改楼宇
     * 
     * @param pmsBuilding 楼宇信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:building:edit')")
    @Log(title = "楼宇管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsBuilding pmsBuilding)
    {
        // 设置更新人
        pmsBuilding.setUpdateBy(getUsername());
        // 更新记录并返回操作结果
        return toAjax(pmsBuildingService.updatePmsBuilding(pmsBuilding));
    }

    /**
     * 删除楼宇
     * 
     * @param buildingIds 需要删除的楼宇主键数组
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:building:remove')")
    @Log(title = "楼宇管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{buildingIds}")
    public AjaxResult remove(@PathVariable Long[] buildingIds)
    {
        // 批量删除楼宇记录
        return toAjax(pmsBuildingService.deletePmsBuildingByBuildingIds(buildingIds));
    }
}
