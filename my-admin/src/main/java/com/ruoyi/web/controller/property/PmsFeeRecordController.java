package com.ruoyi.web.controller.property;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.property.domain.PmsBuilding;
import com.ruoyi.property.domain.PmsFeeRecord;
import com.ruoyi.property.domain.PmsFeeType;
import com.ruoyi.property.domain.PmsOwner;
import com.ruoyi.property.domain.PmsRoom;
import com.ruoyi.property.service.IPmsBuildingService;
import com.ruoyi.property.service.IPmsFeeRecordService;
import com.ruoyi.property.service.IPmsFeeTypeService;
import com.ruoyi.property.service.IPmsOwnerService;
import com.ruoyi.property.service.IPmsRoomService;

/**
 * 物业费用记录控制器
 *
 * 负责物业费账单的增删改查、月度账单批量生成、缴费确认和催缴提醒。
 * 核心的业务逻辑都在这个控制器里了。
 *
 * @author 课程设计小组
 */
@RestController
@RequestMapping("/property/feeRecord")
public class PmsFeeRecordController extends BaseController {

    @Autowired
    private IPmsFeeRecordService pmsFeeRecordService;

    @Autowired
    private IPmsRoomService pmsRoomService;

    @Autowired
    private IPmsFeeTypeService pmsFeeTypeService;

    @Autowired
    private IPmsOwnerService pmsOwnerService;

    @Autowired
    private IPmsBuildingService pmsBuildingService;

    /**
     * 分页查询费用记录列表
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsFeeRecord pmsFeeRecord) {
        startPage();
        List<PmsFeeRecord> list = pmsFeeRecordService.selectPmsFeeRecordList(pmsFeeRecord);
        return getDataTable(list);
    }

    /**
     * 根据ID获取单条费用记录详情
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId) {
        // 查询账单详情，前端用来展示或者修改
        return success(pmsFeeRecordService.selectPmsFeeRecordByRecordId(recordId));
    }

    /**
     * 新增费用记录（手工添加）
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:add')")
    @Log(title = "费用记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsFeeRecord pmsFeeRecord) {
        pmsFeeRecord.setCreateBy(getUsername());
        return toAjax(pmsFeeRecordService.insertPmsFeeRecord(pmsFeeRecord));
    }

    /**
     * 修改费用记录
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:edit')")
    @Log(title = "费用记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsFeeRecord pmsFeeRecord) {
        pmsFeeRecord.setUpdateBy(getUsername());
        return toAjax(pmsFeeRecordService.updatePmsFeeRecord(pmsFeeRecord));
    }

    /**
     * 删除费用记录
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:remove')")
    @Log(title = "费用记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(pmsFeeRecordService.deletePmsFeeRecordByRecordIds(recordIds));
    }

    /**
     * 获取收费统计数据
     * 包括本月应收、实收、待收金额、收缴率等
     * TODO: 目前是实时计算，数据量大时会影响性能，后续可以考虑加缓存
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:list')")
    @GetMapping("/stats")
    public AjaxResult getStats(@RequestParam(value = "month", required = false) String month) {
        if (month == null || month.trim().isEmpty()) {
            month = new SimpleDateFormat("yyyy-MM").format(new Date());
        }

        PmsFeeRecord query = new PmsFeeRecord();
        query.setFeeMonth(month);
        List<PmsFeeRecord> records = pmsFeeRecordService.selectPmsFeeRecordList(query);

        BigDecimal totalPayable = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        Set<Long> unpaidOwners = new HashSet<>();

        int todayPaidCount = 0;
        BigDecimal todayPaidAmount = BigDecimal.ZERO;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todayStr = sdf.format(new Date());

        for (PmsFeeRecord r : records) {
            if (r.getFeeAmount() != null) {
                totalPayable = totalPayable.add(r.getFeeAmount());
            }
            if (r.getPaidAmount() != null) {
                totalPaid = totalPaid.add(r.getPaidAmount());
            }
            if ("0".equals(r.getPayStatus()) || "2".equals(r.getPayStatus())) {
                unpaidOwners.add(r.getOwnerId());
            }

            if (r.getPayTime() != null && todayStr.equals(sdf.format(r.getPayTime()))) {
                todayPaidCount++;
                if (r.getPaidAmount() != null) {
                    todayPaidAmount = todayPaidAmount.add(r.getPaidAmount());
                }
            }
        }

        BigDecimal totalUnpaid = totalPayable.subtract(totalPaid);
        if (totalUnpaid.compareTo(BigDecimal.ZERO) < 0) {
            totalUnpaid = BigDecimal.ZERO;
        }

        BigDecimal payRate = BigDecimal.ZERO;
        if (totalPayable.compareTo(BigDecimal.ZERO) > 0) {
            payRate = totalPaid.multiply(new BigDecimal("100"))
                               .divide(totalPayable, 1, RoundingMode.HALF_UP);
        }

        AjaxResult ajax = AjaxResult.success();
        ajax.put("totalPayable", totalPayable);
        ajax.put("totalPaid", totalPaid);
        ajax.put("totalUnpaid", totalUnpaid);
        ajax.put("payRate", payRate);
        ajax.put("unpaidHouseholds", unpaidOwners.size());
        ajax.put("todayPaidCount", todayPaidCount);
        ajax.put("todayPaidAmount", todayPaidAmount);

        return ajax;
    }

    @PreAuthorize("@ss.hasPermi('property:feeRecord:add')")
    @Log(title = "费用记录", businessType = BusinessType.INSERT)
    @PostMapping("/generateMonthly")
    public AjaxResult generateMonthly(@RequestParam("month") String month) {
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            return AjaxResult.error("月份格式错误，应为 YYYY-MM");
        }

        PmsRoom roomQuery = new PmsRoom();
        roomQuery.setStatus("2");
        List<PmsRoom> rooms = pmsRoomService.selectPmsRoomList(roomQuery);
        if (rooms.isEmpty()) {
            roomQuery.setStatus(null);
            rooms = pmsRoomService.selectPmsRoomList(roomQuery);
        }

        PmsFeeType feeTypeQuery = new PmsFeeType();
        feeTypeQuery.setTypeCode("WYLF");
        List<PmsFeeType> feeTypes = pmsFeeTypeService.selectPmsFeeTypeList(feeTypeQuery);
        PmsFeeType feeType = (feeTypes != null && !feeTypes.isEmpty()) ? feeTypes.get(0) : null;
        if (feeType == null) {
            return AjaxResult.error("未找到物业管理费费用类型，请先在费用类型管理中配置");
        }

        int generatedCount = 0;

        for (PmsRoom room : rooms) {
            PmsFeeRecord recordQuery = new PmsFeeRecord();
            recordQuery.setRoomId(room.getRoomId());
            recordQuery.setFeeMonth(month);
            recordQuery.setFeeTypeId(feeType.getFeeTypeId());
            List<PmsFeeRecord> existing = pmsFeeRecordService.selectPmsFeeRecordList(recordQuery);

            if (existing != null && !existing.isEmpty()) {
                continue;
            }

            PmsFeeRecord record = new PmsFeeRecord();
            Long communityId = null;
            if (room.getBuildingId() != null) {
                PmsBuilding building = pmsBuildingService.selectPmsBuildingByBuildingId(room.getBuildingId());
                if (building != null && building.getCommunityId() != null) {
                    communityId = building.getCommunityId();
                }
            }

            record.setCommunityId(communityId);
            record.setRoomId(room.getRoomId());

            Long ownerId = null;
            PmsFeeRecord historyQuery = new PmsFeeRecord();
            historyQuery.setRoomId(room.getRoomId());
            List<PmsFeeRecord> histories = pmsFeeRecordService.selectPmsFeeRecordList(historyQuery);
            if (histories != null && !histories.isEmpty()) {
                for (PmsFeeRecord h : histories) {
                    if (h.getOwnerId() != null) {
                        ownerId = h.getOwnerId();
                        break;
                    }
                }
            }

            record.setOwnerId(ownerId);
            record.setFeeTypeId(feeType.getFeeTypeId());

            BigDecimal area = room.getConstructionArea();
            if (area == null || area.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal amount = area.multiply(feeType.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);

            record.setFeeAmount(amount);
            record.setPaidAmount(BigDecimal.ZERO);
            record.setFeeMonth(month);
            record.setPayStatus("0");
            record.setCreateBy(getUsername());

            pmsFeeRecordService.insertPmsFeeRecord(record);
            generatedCount++;
        }

        return AjaxResult.success("成功为 " + generatedCount + " 户房屋生成 " + month + " 月度账单");
    }

    @PreAuthorize("@ss.hasPermi('property:feeRecord:edit')")
    @PostMapping("/remindAll")
    public AjaxResult remindAll(@RequestParam("month") String month) {
        PmsFeeRecord query = new PmsFeeRecord();
        query.setFeeMonth(month);
        query.setPayStatus("0");
        List<PmsFeeRecord> unpaidRecords = pmsFeeRecordService.selectPmsFeeRecordList(query);

        int count = 0;
        List<String> logs = new ArrayList<>();

        for (PmsFeeRecord r : unpaidRecords) {
            String ownerName = r.getOwnerName();
            if (r.getOwnerId() != null) {
                PmsOwner owner = pmsOwnerService.selectPmsOwnerByOwnerId(r.getOwnerId());
                if (owner != null && ownerName == null) {
                    ownerName = owner.getOwnerName();
                }
            }
            if (ownerName == null) {
                ownerName = "业主";
            }

            logs.add("待催缴: " + ownerName + " - " + month + "月物业费 ¥" + r.getFeeAmount());
            count++;
        }

        AjaxResult ajax = AjaxResult.success("批量催缴提醒发送完成，共 " + count + " 户");
        ajax.put("logs", logs);
        return ajax;
    }

    @PreAuthorize("@ss.hasPermi('property:feeRecord:edit')")
    @Log(title = "费用记录", businessType = BusinessType.UPDATE)
    @PostMapping("/payQuick")
    public AjaxResult payQuick(@RequestParam("recordId") Long recordId) {
        PmsFeeRecord record = pmsFeeRecordService.selectPmsFeeRecordByRecordId(recordId);
        if (record == null) {
            return AjaxResult.error("账单记录不存在");
        }

        record.setPayStatus("1");
        record.setPaidAmount(record.getFeeAmount());
        record.setPayTime(new Date());
        record.setUpdateBy(getUsername());

        return toAjax(pmsFeeRecordService.updatePmsFeeRecord(record));
    }

    @PreAuthorize("@ss.hasPermi('property:feeRecord:edit')")
    @Log(title = "费用记录", businessType = BusinessType.UPDATE)
    @PostMapping("/payBatch")
    public AjaxResult payBatch(@RequestParam("recordIds") Long[] recordIds) {
        if (recordIds == null || recordIds.length == 0) {
            return AjaxResult.error("请选择要收款的账单记录");
        }
        int count = 0;
        for (Long id : recordIds) {
            PmsFeeRecord record = pmsFeeRecordService.selectPmsFeeRecordByRecordId(id);
            if (record != null && !"1".equals(record.getPayStatus())) {
                record.setPayStatus("1");
                record.setPaidAmount(record.getFeeAmount());
                record.setPayTime(new Date());
                record.setUpdateBy(getUsername());
                pmsFeeRecordService.updatePmsFeeRecord(record);
                count++;
            }
        }
        return AjaxResult.success("批量收款成功，已确认 " + count + " 笔账单");
    }
}
