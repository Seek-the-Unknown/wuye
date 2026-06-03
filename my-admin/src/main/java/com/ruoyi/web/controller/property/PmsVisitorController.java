package com.ruoyi.web.controller.property;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsVisitor;
import com.ruoyi.property.service.IPmsVisitorService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/visitor")
public class PmsVisitorController extends BaseController {
    @Autowired
    private IPmsVisitorService pmsVisitorService;

    @PreAuthorize("@ss.hasPermi('property:visitor:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsVisitor pmsVisitor) {
        startPage();
        List<PmsVisitor> list = pmsVisitorService.selectPmsVisitorList(pmsVisitor);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:visitor:query')")
    @GetMapping("/{visitorId}")
    public AjaxResult getInfo(@PathVariable Long visitorId) {
        return success(pmsVisitorService.selectPmsVisitorByVisitorId(visitorId));
    }

    @PreAuthorize("@ss.hasPermi('property:visitor:add')")
    @Log(title = "访客管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsVisitor pmsVisitor) {
        pmsVisitor.setCreateBy(getUsername());
        return toAjax(pmsVisitorService.insertPmsVisitor(pmsVisitor));
    }

    @PreAuthorize("@ss.hasPermi('property:visitor:edit')")
    @Log(title = "访客管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsVisitor pmsVisitor) {
        pmsVisitor.setUpdateBy(getUsername());
        return toAjax(pmsVisitorService.updatePmsVisitor(pmsVisitor));
    }

    @PreAuthorize("@ss.hasPermi('property:visitor:remove')")
    @Log(title = "访客管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{visitorIds}")
    public AjaxResult remove(@PathVariable Long[] visitorIds) {
        return toAjax(pmsVisitorService.deletePmsVisitorByVisitorIds(visitorIds));
    }
}
