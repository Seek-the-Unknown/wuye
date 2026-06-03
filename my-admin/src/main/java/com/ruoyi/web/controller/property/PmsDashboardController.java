package com.ruoyi.web.controller.property;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.property.domain.PmsCommunity;
import com.ruoyi.property.domain.PmsComplaint;
import com.ruoyi.property.domain.PmsFeeRecord;
import com.ruoyi.property.domain.PmsOwner;
import com.ruoyi.property.domain.PmsRepair;
import com.ruoyi.property.domain.PmsRoom;
import com.ruoyi.property.domain.PmsVisitor;
import com.ruoyi.property.mapper.PmsCommunityMapper;
import com.ruoyi.property.mapper.PmsComplaintMapper;
import com.ruoyi.property.mapper.PmsFeeRecordMapper;
import com.ruoyi.property.mapper.PmsOwnerMapper;
import com.ruoyi.property.mapper.PmsRepairMapper;
import com.ruoyi.property.mapper.PmsRoomMapper;
import com.ruoyi.property.mapper.PmsVisitorMapper;

@RestController
@RequestMapping("/property/dashboard")
public class PmsDashboardController extends BaseController {

    @Autowired
    private PmsCommunityMapper communityMapper;

    @Autowired
    private PmsOwnerMapper ownerMapper;

    @Autowired
    private PmsRoomMapper roomMapper;

    @Autowired
    private PmsRepairMapper repairMapper;

    @Autowired
    private PmsFeeRecordMapper feeRecordMapper;

    @Autowired
    private PmsComplaintMapper complaintMapper;

    @Autowired
    private PmsVisitorMapper visitorMapper;

    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/stats")
    public AjaxResult getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("communityCount", communityMapper.selectPmsCommunityList(new PmsCommunity()).size());
        stats.put("ownerCount", ownerMapper.selectPmsOwnerList(new PmsOwner()).size());
        stats.put("roomCount", roomMapper.selectPmsRoomList(new PmsRoom()).size());
        stats.put("repairCount", repairMapper.selectPmsRepairList(new PmsRepair()).size());
        stats.put("complaintCount", complaintMapper.selectPmsComplaintList(new PmsComplaint()).size());
        stats.put("visitorCount", visitorMapper.selectPmsVisitorList(new PmsVisitor()).size());

        PmsRepair pendingRepairQuery = new PmsRepair();
        pendingRepairQuery.setRepairStatus("0");
        stats.put("pendingRepairCount", repairMapper.selectPmsRepairList(pendingRepairQuery).size());

        PmsFeeRecord unpaidFeeQuery = new PmsFeeRecord();
        unpaidFeeQuery.setPayStatus("0");
        stats.put("unpaidFeeCount", feeRecordMapper.selectPmsFeeRecordList(unpaidFeeQuery).size());

        return success(stats);
    }

    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/recentRepairs")
    public AjaxResult getRecentRepairs() {
        List<PmsRepair> allRepairs = repairMapper.selectPmsRepairList(new PmsRepair());
        List<PmsRepair> recentRepairs = allRepairs.stream()
                .sorted(Comparator.comparing(PmsRepair::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(8)
                .collect(Collectors.toList());
        return success(recentRepairs);
    }

    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/repairTrend")
    public AjaxResult getRepairTrend() {
        List<PmsRepair> allRepairs = repairMapper.selectPmsRepairList(new PmsRepair());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth currentMonth = YearMonth.now();

        List<Map<String, Object>> trendData = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            String monthStr = targetMonth.format(formatter);

            long count = allRepairs.stream()
                    .filter(r -> r.getCreateTime() != null)
                    .filter(r -> {
                        LocalDate createDate = r.getCreateTime().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate();
                        YearMonth createMonth = YearMonth.from(createDate);
                        return createMonth.equals(targetMonth);
                    })
                    .count();

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", monthStr);
            monthData.put("count", count);
            trendData.add(monthData);
        }
        return success(trendData);
    }

    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/feeCollection")
    public AjaxResult getFeeCollection() {
        List<PmsFeeRecord> allFeeRecords = feeRecordMapper.selectPmsFeeRecordList(new PmsFeeRecord());

        BigDecimal totalAmount = allFeeRecords.stream()
                .map(PmsFeeRecord::getFeeAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidAmount = allFeeRecords.stream()
                .filter(r -> "1".equals(r.getPayStatus()))
                .map(PmsFeeRecord::getPaidAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);

        long totalCount = allFeeRecords.size();
        long paidCount = allFeeRecords.stream()
                .filter(r -> "1".equals(r.getPayStatus()))
                .count();

        BigDecimal payRate = BigDecimal.ZERO;
        if (totalCount > 0) {
            payRate = new BigDecimal(paidCount)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", totalAmount);
        result.put("paidAmount", paidAmount);
        result.put("unpaidAmount", unpaidAmount);
        result.put("payRate", payRate);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/todoSummary")
    public AjaxResult getTodoSummary() {
        PmsRepair pendingRepairQuery = new PmsRepair();
        pendingRepairQuery.setRepairStatus("0");
        int pendingRepairs = repairMapper.selectPmsRepairList(pendingRepairQuery).size();

        PmsRepair processingRepairQuery = new PmsRepair();
        processingRepairQuery.setRepairStatus("1");
        int processingRepairs = repairMapper.selectPmsRepairList(processingRepairQuery).size();

        List<PmsComplaint> allComplaints = complaintMapper.selectPmsComplaintList(new PmsComplaint());
        long pendingComplaints = allComplaints.stream()
                .filter(c -> "0".equals(c.getHandleStatus()) || "1".equals(c.getHandleStatus()))
                .count();

        PmsVisitor pendingVisitorQuery = new PmsVisitor();
        pendingVisitorQuery.setVisitStatus("0");
        int pendingVisitors = visitorMapper.selectPmsVisitorList(pendingVisitorQuery).size();

        PmsFeeRecord unpaidFeeQuery = new PmsFeeRecord();
        unpaidFeeQuery.setPayStatus("0");
        int unpaidFees = feeRecordMapper.selectPmsFeeRecordList(unpaidFeeQuery).size();

        Map<String, Object> result = new HashMap<>();
        result.put("pendingRepairs", pendingRepairs);
        result.put("processingRepairs", processingRepairs);
        result.put("pendingComplaints", pendingComplaints);
        result.put("pendingVisitors", pendingVisitors);
        result.put("unpaidFees", unpaidFees);
        return success(result);
    }
}
