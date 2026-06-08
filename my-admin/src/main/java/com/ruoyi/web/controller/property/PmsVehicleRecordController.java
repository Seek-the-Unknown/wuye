package com.ruoyi.web.controller.property;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsFeeType;
import com.ruoyi.property.domain.PmsVehicleRecord;
import com.ruoyi.property.service.IPmsFeeTypeService;
import com.ruoyi.property.service.IPmsVehicleRecordService;

@RestController
@RequestMapping("/property/vehicleRecord")
public class PmsVehicleRecordController extends BaseController {

    @Autowired
    private IPmsVehicleRecordService pmsVehicleRecordService;

    @Autowired
    private IPmsFeeTypeService pmsFeeTypeService;

    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsVehicleRecord pmsVehicleRecord) {
        startPage();
        List<PmsVehicleRecord> list = pmsVehicleRecordService.selectPmsVehicleRecordList(pmsVehicleRecord);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId) {
        return success(pmsVehicleRecordService.selectPmsVehicleRecordByRecordId(recordId));
    }

    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:add')")
    @Log(title = "车辆进出记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsVehicleRecord pmsVehicleRecord) {
        pmsVehicleRecord.setCreateBy(getUsername());
        return toAjax(pmsVehicleRecordService.insertPmsVehicleRecord(pmsVehicleRecord));
    }

    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:edit')")
    @Log(title = "车辆进出记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsVehicleRecord pmsVehicleRecord) {
        pmsVehicleRecord.setUpdateBy(getUsername());
        return toAjax(pmsVehicleRecordService.updatePmsVehicleRecord(pmsVehicleRecord));
    }

    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:remove')")
    @Log(title = "车辆进出记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(pmsVehicleRecordService.deletePmsVehicleRecordByRecordIds(recordIds));
    }

    /**
     * 车辆入场 - 拍照识别车牌 + 创建入场记录
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:enter')")
    @Log(title = "车辆入场识别", businessType = BusinessType.INSERT)
    @PostMapping("/enter")
    public AjaxResult enter(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "communityId", required = false) Long communityId,
        @RequestParam(value = "parkingId", required = false) Long parkingId,
        @RequestParam(value = "plateNumber", required = false) String plateNumber) {
        // 必须提供文件或手动输入车牌号
        boolean hasFile = file != null && !file.isEmpty();
        boolean hasPlate = plateNumber != null && !plateNumber.trim().isEmpty();
        if (!hasFile && !hasPlate) {
            return AjaxResult.error("请上传车辆照片或手动输入车牌号");
        }
        PmsVehicleRecord record = pmsVehicleRecordService.handleVehicleEnter(file, communityId, parkingId, plateNumber);
        return success(record);
    }

    /**
     * 车辆出场 - 拍照识别车牌 + 计时计费 + 完成出场
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:exit')")
    @Log(title = "车辆出场计费", businessType = BusinessType.UPDATE)
    @PostMapping("/exit")
    public AjaxResult exit(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "plateNumber", required = false) String plateNumber) {
        boolean hasFile = file != null && !file.isEmpty();
        boolean hasPlate = plateNumber != null && !plateNumber.trim().isEmpty();
        if (!hasFile && !hasPlate) {
            return AjaxResult.error("请上传车辆照片或手动输入车牌号");
        }
        PmsVehicleRecord record = pmsVehicleRecordService.handleVehicleExit(file, plateNumber);
        return success(record);
    }

    /**
     * 获取停车计费单价（从 pms_fee_type 中读取 type_code='PARKING' 的单价）
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:list')")
    @GetMapping("/getHourlyRate")
    public AjaxResult getHourlyRate() {
        PmsFeeType query = new PmsFeeType();
        query.setTypeCode("PARKING");
        List<PmsFeeType> types = pmsFeeTypeService.selectPmsFeeTypeList(query);
        if (types != null && !types.isEmpty()) {
            PmsFeeType parking = types.get(0);
            return AjaxResult.success().put("unitPrice", parking.getUnitPrice());
        }
        return AjaxResult.success().put("unitPrice", BigDecimal.valueOf(5));
    }

    /**
     * 按车牌号查找活动入场记录
     */
    @GetMapping("/searchActive")
    public AjaxResult searchActive(@RequestParam("plateNumber") String plateNumber) {
        PmsVehicleRecord record = pmsVehicleRecordService.selectActiveRecordByPlate(plateNumber);
        if (record == null) {
            return AjaxResult.error("未找到车牌号【" + plateNumber + "】的入场记录");
        }
        return success(record);
    }
}
