package com.ruoyi.web.controller.property;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.property.domain.PmsRepair;
import com.ruoyi.property.service.IPmsRepairService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/portal/worker")
public class PmsWorkerPortalController extends BaseController {

    @Autowired
    private IPmsRepairService pmsRepairService;

    @PreAuthorize("@ss.hasPermi('property:worker:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsRepair query) {
        startPage();
        query.setWorkerId(getUserId());
        List<PmsRepair> list = pmsRepairService.selectPmsRepairList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:worker:list')")
    @PutMapping("/finish/{repairId}")
    public AjaxResult finish(@PathVariable Long repairId) {
        PmsRepair repair = pmsRepairService.selectPmsRepairByRepairId(repairId);
        if (repair == null) {
            return error("未找到该报修工单");
        }
        if (!getUserId().equals(repair.getWorkerId())) {
            return error("您无权操作非分配给您的工单任务");
        }
        repair.setFinishTime(new Date());
        repair.setRepairStatus("2");
        repair.setUpdateBy(getUsername());
        return toAjax(pmsRepairService.updatePmsRepair(repair));
    }

    @PreAuthorize("@ss.hasPermi('property:worker:list')")
    @PutMapping("/accept/{repairId}")
    public AjaxResult accept(@PathVariable Long repairId) {
        PmsRepair repair = pmsRepairService.selectPmsRepairByRepairId(repairId);
        if (repair == null) {
            return error("未找到该报修工单");
        }
        if (!getUserId().equals(repair.getWorkerId())) {
            return error("您无权操作非分配给您的工单任务");
        }
        repair.setRepairStatus("1");
        repair.setUpdateBy(getUsername());
        return toAjax(pmsRepairService.updatePmsRepair(repair));
    }
}
