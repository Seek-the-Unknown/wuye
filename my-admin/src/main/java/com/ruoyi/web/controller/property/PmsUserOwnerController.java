package com.ruoyi.web.controller.property;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsUserOwner;
import com.ruoyi.property.service.IPmsUserOwnerService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/userOwner")
public class PmsUserOwnerController extends BaseController {

    @Autowired
    private IPmsUserOwnerService pmsUserOwnerService;

    @PreAuthorize("@ss.hasPermi('property:userOwner:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsUserOwner query) {
        startPage();
        List<PmsUserOwner> list = pmsUserOwnerService.selectPmsUserOwnerList(query);
        return getDataTable(list);
    }

    @GetMapping("/check")
    public AjaxResult checkBinding() {
        return success(pmsUserOwnerService.selectByUserId(getUserId()));
    }

    @PreAuthorize("@ss.hasPermi('property:userOwner:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(pmsUserOwnerService.selectPmsUserOwnerById(id));
    }

    @PreAuthorize("@ss.hasPermi('property:userOwner:add')")
    @Log(title = "用户业主绑定", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsUserOwner pmsUserOwner) {
        pmsUserOwner.setCreateBy(getUsername());
        return toAjax(pmsUserOwnerService.insertPmsUserOwner(pmsUserOwner));
    }

    @PreAuthorize("@ss.hasPermi('property:userOwner:edit')")
    @Log(title = "用户业主绑定", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsUserOwner pmsUserOwner) {
        return toAjax(pmsUserOwnerService.updatePmsUserOwner(pmsUserOwner));
    }

    @PreAuthorize("@ss.hasPermi('property:userOwner:remove')")
    @Log(title = "用户业主绑定", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += pmsUserOwnerService.deletePmsUserOwnerById(id);
        }
        return toAjax(rows);
    }
}
