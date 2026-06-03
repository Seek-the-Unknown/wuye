package com.ruoyi.web.controller.property;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsParking;
import com.ruoyi.property.service.IPmsParkingService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/parking")
public class PmsParkingController extends BaseController {
    @Autowired
    private IPmsParkingService pmsParkingService;

    @PreAuthorize("@ss.hasPermi('property:parking:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsParking pmsParking) {
        startPage();
        List<PmsParking> list = pmsParkingService.selectPmsParkingList(pmsParking);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:parking:query')")
    @GetMapping("/{parkingId}")
    public AjaxResult getInfo(@PathVariable Long parkingId) {
        return success(pmsParkingService.selectPmsParkingByParkingId(parkingId));
    }

    @PreAuthorize("@ss.hasPermi('property:parking:add')")
    @Log(title = "停车位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsParking pmsParking) {
        pmsParking.setCreateBy(getUsername());
        return toAjax(pmsParkingService.insertPmsParking(pmsParking));
    }

    @PreAuthorize("@ss.hasPermi('property:parking:edit')")
    @Log(title = "停车位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsParking pmsParking) {
        pmsParking.setUpdateBy(getUsername());
        return toAjax(pmsParkingService.updatePmsParking(pmsParking));
    }

    @PreAuthorize("@ss.hasPermi('property:parking:remove')")
    @Log(title = "停车位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{parkingIds}")
    public AjaxResult remove(@PathVariable Long[] parkingIds) {
        return toAjax(pmsParkingService.deletePmsParkingByParkingIds(parkingIds));
    }
}
