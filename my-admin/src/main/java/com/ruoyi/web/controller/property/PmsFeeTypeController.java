package com.ruoyi.web.controller.property;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsFeeType;
import com.ruoyi.property.service.IPmsFeeTypeService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/feeType")
public class PmsFeeTypeController extends BaseController {
    @Autowired
    private IPmsFeeTypeService pmsFeeTypeService;

    @PreAuthorize("@ss.hasPermi('property:feeType:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsFeeType pmsFeeType) {
        startPage();
        List<PmsFeeType> list = pmsFeeTypeService.selectPmsFeeTypeList(pmsFeeType);
        return getDataTable(list);
    }

    /** 获取全部（不分页，供下拉选用） */
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        PmsFeeType query = new PmsFeeType();
        query.setStatus("0");
        return success(pmsFeeTypeService.selectPmsFeeTypeList(query));
    }

    @PreAuthorize("@ss.hasPermi('property:feeType:query')")
    @GetMapping("/{feeTypeId}")
    public AjaxResult getInfo(@PathVariable Long feeTypeId) {
        return success(pmsFeeTypeService.selectPmsFeeTypeByFeeTypeId(feeTypeId));
    }

    @PreAuthorize("@ss.hasPermi('property:feeType:add')")
    @Log(title = "费用类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsFeeType pmsFeeType) {
        pmsFeeType.setCreateBy(getUsername());
        return toAjax(pmsFeeTypeService.insertPmsFeeType(pmsFeeType));
    }

    @PreAuthorize("@ss.hasPermi('property:feeType:edit')")
    @Log(title = "费用类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsFeeType pmsFeeType) {
        pmsFeeType.setUpdateBy(getUsername());
        return toAjax(pmsFeeTypeService.updatePmsFeeType(pmsFeeType));
    }

    @PreAuthorize("@ss.hasPermi('property:feeType:remove')")
    @Log(title = "费用类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{feeTypeIds}")
    public AjaxResult remove(@PathVariable Long[] feeTypeIds) {
        return toAjax(pmsFeeTypeService.deletePmsFeeTypeByFeeTypeIds(feeTypeIds));
    }
}
