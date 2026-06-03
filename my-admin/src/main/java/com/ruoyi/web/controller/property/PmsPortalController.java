package com.ruoyi.web.controller.property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.property.domain.PmsComplaint;
import com.ruoyi.property.domain.PmsFeeRecord;
import com.ruoyi.property.domain.PmsNotice;
import com.ruoyi.property.domain.PmsParking;
import com.ruoyi.property.domain.PmsRepair;
import com.ruoyi.property.domain.PmsRoom;
import com.ruoyi.property.domain.PmsUserOwner;
import com.ruoyi.property.domain.PmsVisitor;
import com.ruoyi.property.service.IPmsComplaintService;
import com.ruoyi.property.service.IPmsFeeRecordService;
import com.ruoyi.property.service.IPmsNoticeService;
import com.ruoyi.property.service.IPmsParkingService;
import com.ruoyi.property.service.IPmsRepairService;
import com.ruoyi.property.service.IPmsRoomService;
import com.ruoyi.property.service.IPmsUserOwnerService;
import com.ruoyi.property.service.IPmsVisitorService;

/**
 * 业主门户控制器
 *
 * 这个控制器负责处理业主自助门户的所有请求，
 * 包括查看房屋、账单、报修、访客登记、投诉建议等功能。
 *
 * 注意：业主必须先通过用户-业主绑定才能使用门户功能，
 * 否则会抛出"当前用户未绑定业主信息"异常。
 *
 * @author 课程设计小组
 */
@RestController
@RequestMapping("/property/portal")
public class PmsPortalController extends BaseController {

    @Autowired
    private IPmsUserOwnerService userOwnerService;
    @Autowired
    private IPmsRoomService roomService;
    @Autowired
    private IPmsFeeRecordService feeRecordService;
    @Autowired
    private IPmsRepairService repairService;
    @Autowired
    private IPmsVisitorService visitorService;
    @Autowired
    private IPmsNoticeService noticeService;
    @Autowired
    private IPmsParkingService parkingService;
    @Autowired
    private IPmsComplaintService complaintService;

    /** 获取当前登录用户的业主ID */
    private Long getCurrentOwnerId() {
        Long userId = getUserId();
        PmsUserOwner binding = userOwnerService.selectByUserId(userId);
        if (binding == null) {
            throw new RuntimeException("当前用户未绑定业主信息");
        }
        return binding.getOwnerId();
    }

    @PreAuthorize("@ss.hasPermi('property:portal:room')")
    @GetMapping("/myRoom")
    public TableDataInfo myRoom() {
        startPage();
        Long ownerId = getCurrentOwnerId();
        PmsRoom query = new PmsRoom();
        query.setOwnerId(ownerId);
        List<PmsRoom> list = roomService.selectPmsRoomList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:portal:bill')")
    @GetMapping("/myBill")
    public TableDataInfo myBill(PmsFeeRecord query) {
        startPage();
        query.setOwnerId(getCurrentOwnerId());
        List<PmsFeeRecord> list = feeRecordService.selectPmsFeeRecordList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:portal:repair')")
    @GetMapping("/myRepair")
    public TableDataInfo myRepair(PmsRepair query) {
        startPage();
        query.setOwnerId(getCurrentOwnerId());
        List<PmsRepair> list = repairService.selectPmsRepairList(query);
        return getDataTable(list);
    }
    
    @PreAuthorize("@ss.hasPermi('property:portal:repair')")
    @PostMapping("/repair")
    public AjaxResult addRepair(@RequestBody PmsRepair repair) {
        Long ownerId = getCurrentOwnerId();
        repair.setOwnerId(ownerId);
        if (repair.getCommunityId() == null) {
            PmsFeeRecord query = new PmsFeeRecord();
            query.setOwnerId(ownerId);
            List<PmsFeeRecord> records = feeRecordService.selectPmsFeeRecordList(query);
            if (!records.isEmpty() && records.get(0).getCommunityId() != null) {
                repair.setCommunityId(records.get(0).getCommunityId());
            }
        }
        repair.setCreateBy(getUsername());
        return toAjax(repairService.insertPmsRepair(repair));
    }

    @PreAuthorize("@ss.hasPermi('property:portal:visitor')")
    @GetMapping("/myVisitor")
    public TableDataInfo myVisitor(PmsVisitor query) {
        startPage();
        query.setOwnerId(getCurrentOwnerId());
        List<PmsVisitor> list = visitorService.selectPmsVisitorList(query);
        return getDataTable(list);
    }
    
    @PreAuthorize("@ss.hasPermi('property:portal:visitor')")
    @PostMapping("/visitor")
    public AjaxResult addVisitor(@RequestBody PmsVisitor visitor) {
        Long ownerId = getCurrentOwnerId();
        visitor.setOwnerId(ownerId);
        if (visitor.getCommunityId() == null) {
            PmsFeeRecord query = new PmsFeeRecord();
            query.setOwnerId(ownerId);
            List<PmsFeeRecord> records = feeRecordService.selectPmsFeeRecordList(query);
            if (!records.isEmpty() && records.get(0).getCommunityId() != null) {
                visitor.setCommunityId(records.get(0).getCommunityId());
            }
        }
        visitor.setCreateBy(getUsername());
        return toAjax(visitorService.insertPmsVisitor(visitor));
    }

    @PreAuthorize("@ss.hasPermi('property:portal:parking')")
    @GetMapping("/myParking")
    public TableDataInfo myParking(PmsParking query) {
        startPage();
        query.setOwnerId(getCurrentOwnerId());
        List<PmsParking> list = parkingService.selectPmsParkingList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('property:portal:complaint')")
    @GetMapping("/myComplaint")
    public TableDataInfo myComplaint(PmsComplaint query) {
        startPage();
        query.setOwnerId(getCurrentOwnerId());
        List<PmsComplaint> list = complaintService.selectPmsComplaintList(query);
        return getDataTable(list);
    }
    
    @PreAuthorize("@ss.hasPermi('property:portal:complaint')")
    @PostMapping("/complaint")
    public AjaxResult addComplaint(@RequestBody PmsComplaint complaint) {
        Long ownerId = getCurrentOwnerId();
        complaint.setOwnerId(ownerId);
        if (complaint.getCommunityId() == null) {
            PmsFeeRecord query = new PmsFeeRecord();
            query.setOwnerId(ownerId);
            List<PmsFeeRecord> records = feeRecordService.selectPmsFeeRecordList(query);
            if (!records.isEmpty() && records.get(0).getCommunityId() != null) {
                complaint.setCommunityId(records.get(0).getCommunityId());
            }
        }
        complaint.setCreateBy(getUsername());
        return toAjax(complaintService.insertPmsComplaint(complaint));
    }
    
    @PreAuthorize("@ss.hasPermi('property:portal:notice')")
    @GetMapping("/myNotice")
    public TableDataInfo myNotice(PmsNotice query) {
        startPage();
        query.setStatus("1"); // 只能看已发布的
        List<PmsNotice> list = noticeService.selectPmsNoticeList(query);
        return getDataTable(list);
    }
}
