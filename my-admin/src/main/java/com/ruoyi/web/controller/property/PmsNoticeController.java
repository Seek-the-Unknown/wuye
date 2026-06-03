package com.ruoyi.web.controller.property;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsNotice;
import com.ruoyi.property.service.IPmsNoticeService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/notice")
public class PmsNoticeController extends BaseController {
    @Autowired
    private IPmsNoticeService pmsNoticeService;

    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsNotice pmsNotice) {
        startPage();
        List<PmsNotice> list = pmsNoticeService.selectPmsNoticeList(pmsNotice);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:notice:query')")
    @GetMapping("/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId) {
        return success(pmsNoticeService.selectPmsNoticeByNoticeId(noticeId));
    }

    @PreAuthorize("@ss.hasPermi('property:notice:add')")
    @Log(title = "公告管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsNotice pmsNotice) {
        pmsNotice.setCreateBy(getUsername());
        return toAjax(pmsNoticeService.insertPmsNotice(pmsNotice));
    }

    @PreAuthorize("@ss.hasPermi('property:notice:edit')")
    @Log(title = "公告管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsNotice pmsNotice) {
        pmsNotice.setUpdateBy(getUsername());
        return toAjax(pmsNoticeService.updatePmsNotice(pmsNotice));
    }

    @PreAuthorize("@ss.hasPermi('property:notice:remove')")
    @Log(title = "公告管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public AjaxResult remove(@PathVariable Long[] noticeIds) {
        return toAjax(pmsNoticeService.deletePmsNoticeByNoticeIds(noticeIds));
    }
}
