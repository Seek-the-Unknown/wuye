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
import com.ruoyi.property.domain.PmsComplaint;
import com.ruoyi.property.service.IPmsComplaintService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/complaint")
public class PmsComplaintController extends BaseController
{
    @Autowired
    private IPmsComplaintService pmsComplaintService;

    @PreAuthorize("@ss.hasPermi('property:complaint:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsComplaint pmsComplaint)
    {
        startPage();
        List<PmsComplaint> list = pmsComplaintService.selectPmsComplaintList(pmsComplaint);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:complaint:query')")
    @GetMapping("/{complaintId}")
    public AjaxResult getInfo(@PathVariable Long complaintId)
    {
        return success(pmsComplaintService.selectPmsComplaintByComplaintId(complaintId));
    }

    @PreAuthorize("@ss.hasPermi('property:complaint:add')")
    @Log(title = "投诉建议", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsComplaint pmsComplaint)
    {
        pmsComplaint.setCreateBy(getUsername());
        return toAjax(pmsComplaintService.insertPmsComplaint(pmsComplaint));
    }

    @PreAuthorize("@ss.hasPermi('property:complaint:edit')")
    @Log(title = "投诉建议", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsComplaint pmsComplaint)
    {
        pmsComplaint.setUpdateBy(getUsername());
        return toAjax(pmsComplaintService.updatePmsComplaint(pmsComplaint));
    }

    @PreAuthorize("@ss.hasPermi('property:complaint:edit')")
    @Log(title = "处理投诉建议", businessType = BusinessType.UPDATE)
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody PmsComplaint pmsComplaint)
    {
        pmsComplaint.setHandleBy(getUsername());
        pmsComplaint.setHandleTime(new java.util.Date());
        return toAjax(pmsComplaintService.updatePmsComplaint(pmsComplaint));
    }

    @PreAuthorize("@ss.hasPermi('property:complaint:remove')")
    @Log(title = "投诉建议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{complaintIds}")
    public AjaxResult remove(@PathVariable Long[] complaintIds)
    {
        return toAjax(pmsComplaintService.deletePmsComplaintByComplaintIds(complaintIds));
    }
}
