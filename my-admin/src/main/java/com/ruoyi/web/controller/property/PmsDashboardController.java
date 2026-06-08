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

/**
 * 仪表盘（首页数据统计）Controller
 * 
 * 负责提供物业后台首页的各项统计数据
 */
@RestController
@RequestMapping("/property/dashboard")
public class PmsDashboardController extends BaseController {

    /** 小区数据访问接口 */
    @Autowired
    private PmsCommunityMapper communityMapper;

    /** 业主数据访问接口 */
    @Autowired
    private PmsOwnerMapper ownerMapper;

    /** 房屋数据访问接口 */
    @Autowired
    private PmsRoomMapper roomMapper;

    /** 报修数据访问接口 */
    @Autowired
    private PmsRepairMapper repairMapper;

    /** 费用记录数据访问接口 */
    @Autowired
    private PmsFeeRecordMapper feeRecordMapper;

    /** 投诉建议数据访问接口 */
    @Autowired
    private PmsComplaintMapper complaintMapper;

    /** 访客记录数据访问接口 */
    @Autowired
    private PmsVisitorMapper visitorMapper;

    /**
     * 获取总览统计数据
     * 
     * @return 包含各项总数统计结果的响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/stats")
    public AjaxResult getStats() {
        Map<String, Object> stats = new HashMap<>();
        // 统计各项基础数据总量
        stats.put("communityCount", communityMapper.selectPmsCommunityList(new PmsCommunity()).size());
        stats.put("ownerCount", ownerMapper.selectPmsOwnerList(new PmsOwner()).size());
        stats.put("roomCount", roomMapper.selectPmsRoomList(new PmsRoom()).size());
        stats.put("repairCount", repairMapper.selectPmsRepairList(new PmsRepair()).size());
        stats.put("complaintCount", complaintMapper.selectPmsComplaintList(new PmsComplaint()).size());
        stats.put("visitorCount", visitorMapper.selectPmsVisitorList(new PmsVisitor()).size());

        // 统计待处理的报修数量（状态为0）
        PmsRepair pendingRepairQuery = new PmsRepair();
        pendingRepairQuery.setRepairStatus("0");
        stats.put("pendingRepairCount", repairMapper.selectPmsRepairList(pendingRepairQuery).size());

        // 统计未缴费记录数（状态为0）
        PmsFeeRecord unpaidFeeQuery = new PmsFeeRecord();
        unpaidFeeQuery.setPayStatus("0");
        stats.put("unpaidFeeCount", feeRecordMapper.selectPmsFeeRecordList(unpaidFeeQuery).size());

        return success(stats);
    }

    /**
     * 获取近期报修列表
     * 
     * @return 包含近期报修数据的响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/recentRepairs")
    public AjaxResult getRecentRepairs() {
        // 获取所有报修记录
        List<PmsRepair> allRepairs = repairMapper.selectPmsRepairList(new PmsRepair());
        // 按照创建时间降序排序并截取前8条
        List<PmsRepair> recentRepairs = allRepairs.stream()
                .sorted(Comparator.comparing(PmsRepair::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(8)
                .collect(Collectors.toList());
        return success(recentRepairs);
    }

    /**
     * 获取报修趋势统计数据（最近6个月）
     * 
     * @return 包含按月统计的报修数量的响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/repairTrend")
    public AjaxResult getRepairTrend() {
        // 获取所有报修记录
        List<PmsRepair> allRepairs = repairMapper.selectPmsRepairList(new PmsRepair());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth currentMonth = YearMonth.now();

        List<Map<String, Object>> trendData = new ArrayList<>();
        // 循环统计最近6个月的数据
        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            String monthStr = targetMonth.format(formatter);

            // 过滤并计算目标月份的报修数量
            long count = allRepairs.stream()
                    .filter(r -> r.getCreateTime() != null)
                    .filter(r -> {
                        LocalDate createDate = r.getCreateTime().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate();
                        YearMonth createMonth = YearMonth.from(createDate);
                        return createMonth.equals(targetMonth);
                    })
                    .count();

            // 组装月份数据
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", monthStr);
            monthData.put("count", count);
            trendData.add(monthData);
        }
        return success(trendData);
    }

    /**
     * 获取费用收缴统计数据
     * 
     * @return 包含应收、已收、未收金额以及缴费率等统计信息的响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/feeCollection")
    public AjaxResult getFeeCollection() {
        // 获取所有费用记录
        List<PmsFeeRecord> allFeeRecords = feeRecordMapper.selectPmsFeeRecordList(new PmsFeeRecord());

        // 计算总应收金额
        BigDecimal totalAmount = allFeeRecords.stream()
                .map(PmsFeeRecord::getFeeAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算总已收金额（仅统计缴费状态为1的记录）
        BigDecimal paidAmount = allFeeRecords.stream()
                .filter(r -> "1".equals(r.getPayStatus()))
                .map(PmsFeeRecord::getPaidAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算未收金额
        BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);

        long totalCount = allFeeRecords.size();
        // 计算已缴费记录数
        long paidCount = allFeeRecords.stream()
                .filter(r -> "1".equals(r.getPayStatus()))
                .count();

        // 计算缴费率（百分比）
        BigDecimal payRate = BigDecimal.ZERO;
        if (totalCount > 0) {
            payRate = new BigDecimal(paidCount)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
        }

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", totalAmount);
        result.put("paidAmount", paidAmount);
        result.put("unpaidAmount", unpaidAmount);
        result.put("payRate", payRate);
        return success(result);
    }

    /**
     * 获取待办事项汇总统计
     * 
     * @return 包含各类待处理事项数量的响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:dashboard:view')")
    @GetMapping("/todoSummary")
    public AjaxResult getTodoSummary() {
        // 统计待处理报修（状态0）
        PmsRepair pendingRepairQuery = new PmsRepair();
        pendingRepairQuery.setRepairStatus("0");
        int pendingRepairs = repairMapper.selectPmsRepairList(pendingRepairQuery).size();

        // 统计处理中报修（状态1）
        PmsRepair processingRepairQuery = new PmsRepair();
        processingRepairQuery.setRepairStatus("1");
        int processingRepairs = repairMapper.selectPmsRepairList(processingRepairQuery).size();

        // 统计待处理投诉（状态为0或1）
        List<PmsComplaint> allComplaints = complaintMapper.selectPmsComplaintList(new PmsComplaint());
        long pendingComplaints = allComplaints.stream()
                .filter(c -> "0".equals(c.getHandleStatus()) || "1".equals(c.getHandleStatus()))
                .count();

        // 统计待审核访客（状态0）
        PmsVisitor pendingVisitorQuery = new PmsVisitor();
        pendingVisitorQuery.setVisitStatus("0");
        int pendingVisitors = visitorMapper.selectPmsVisitorList(pendingVisitorQuery).size();

        // 统计未缴费账单（状态0）
        PmsFeeRecord unpaidFeeQuery = new PmsFeeRecord();
        unpaidFeeQuery.setPayStatus("0");
        int unpaidFees = feeRecordMapper.selectPmsFeeRecordList(unpaidFeeQuery).size();

        // 封装结果并返回
        Map<String, Object> result = new HashMap<>();
        result.put("pendingRepairs", pendingRepairs);
        result.put("processingRepairs", processingRepairs);
        result.put("pendingComplaints", pendingComplaints);
        result.put("pendingVisitors", pendingVisitors);
        result.put("unpaidFees", unpaidFees);
        return success(result);
    }
}
