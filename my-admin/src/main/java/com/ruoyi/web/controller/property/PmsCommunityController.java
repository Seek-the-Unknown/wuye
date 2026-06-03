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
import com.ruoyi.property.domain.PmsCommunity;
import com.ruoyi.property.service.IPmsCommunityService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 小区管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/property/community")
public class PmsCommunityController extends BaseController
{
    @Autowired
    private IPmsCommunityService pmsCommunityService;

    /**
     * 查询小区管理列表
     */
    @PreAuthorize("@ss.hasPermi('property:community:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsCommunity pmsCommunity)
    {
        startPage();
        List<PmsCommunity> list = pmsCommunityService.selectPmsCommunityList(pmsCommunity);
        return getDataTable(list);
    }

    /** 获取全部小区（不分页，供下拉选用） */
    @GetMapping("/listAll")
    public AjaxResult listAll()
    {
        PmsCommunity query = new PmsCommunity();
        query.setStatus("0");
        return success(pmsCommunityService.selectPmsCommunityList(query));
    }

    /**
     * 导出小区管理列表
     */
    @PreAuthorize("@ss.hasPermi('property:community:export')")
    @Log(title = "小区管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PmsCommunity pmsCommunity)
    {
        List<PmsCommunity> list = pmsCommunityService.selectPmsCommunityList(pmsCommunity);
        ExcelUtil<PmsCommunity> util = new ExcelUtil<PmsCommunity>(PmsCommunity.class);
        util.exportExcel(response, list, "小区管理数据");
    }

    /**
     * 获取小区管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('property:community:query')")
    @GetMapping(value = "/{communityId}")
    public AjaxResult getInfo(@PathVariable("communityId") Long communityId)
    {
        return success(pmsCommunityService.selectPmsCommunityByCommunityId(communityId));
    }

    /**
     * 新增小区管理
     */
    @PreAuthorize("@ss.hasPermi('property:community:add')")
    @Log(title = "小区管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsCommunity pmsCommunity)
    {
        pmsCommunity.setCreateBy(getUsername());
        return toAjax(pmsCommunityService.insertPmsCommunity(pmsCommunity));
    }

    /**
     * 修改小区管理
     */
    @PreAuthorize("@ss.hasPermi('property:community:edit')")
    @Log(title = "小区管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsCommunity pmsCommunity)
    {
        pmsCommunity.setUpdateBy(getUsername());
        return toAjax(pmsCommunityService.updatePmsCommunity(pmsCommunity));
    }

    /**
     * 删除小区管理
     */
    @PreAuthorize("@ss.hasPermi('property:community:remove')")
    @Log(title = "小区管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{communityIds}")
    public AjaxResult remove(@PathVariable Long[] communityIds)
    {
        return toAjax(pmsCommunityService.deletePmsCommunityByCommunityIds(communityIds));
    }
}
