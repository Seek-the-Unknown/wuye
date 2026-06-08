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
     * @param pmsFeeRecord 查询条件实体
     * @return 带有分页信息的表格数据
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsFeeRecord pmsFeeRecord) {
        // 开启分页，拦截器会自动抓取请求中的pageNum和pageSize
        startPage();
        // 根据条件查询费用记录集合
        List<PmsFeeRecord> list = pmsFeeRecordService.selectPmsFeeRecordList(pmsFeeRecord);
        // 返回分页数据对象
        return getDataTable(list);
    }

    /**
     * 根据ID获取单条费用记录详情
     * @param recordId 记录主键ID
     * @return 带有详情数据的标准响应
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId) {
        // 查询账单详情，前端用来展示或者修改弹窗回显数据
        return success(pmsFeeRecordService.selectPmsFeeRecordByRecordId(recordId));
    }

    /**
     * 新增费用记录（手工添加单笔账单）
     * @param pmsFeeRecord 前端传入的费用数据
     * @return 插入结果
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:add')")
    @Log(title = "费用记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsFeeRecord pmsFeeRecord) {
        // 设置创建者为当前系统登录用户名
        pmsFeeRecord.setCreateBy(getUsername());
        return toAjax(pmsFeeRecordService.insertPmsFeeRecord(pmsFeeRecord));
    }

    /**
     * 修改已存在的费用记录（例如管理员修改异常金额）
     * @param pmsFeeRecord 需要更新的费用数据
     * @return 更新结果
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:edit')")
    @Log(title = "费用记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsFeeRecord pmsFeeRecord) {
        // 设置更新者为当前登录用户
        pmsFeeRecord.setUpdateBy(getUsername());
        return toAjax(pmsFeeRecordService.updatePmsFeeRecord(pmsFeeRecord));
    }

    /**
     * 批量或单条删除费用记录
     * @param recordIds 主键ID数组
     * @return 删除结果
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:remove')")
    @Log(title = "费用记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(pmsFeeRecordService.deletePmsFeeRecordByRecordIds(recordIds));
    }

    /**
     * 核心业务：获取当前月份的收费统计看板数据
     * 包括本月应收、实收、待收金额、收缴率、今日收款金额等核心运营指标
     * TODO: 目前是实时遍历所有记录计算，数据量极大时会影响性能，后续可以考虑将汇总数据存入 Redis 缓存或新建统计结果表
     * 
     * @param month 查询月份（格式如 "2026-06"）
     * @return 包含所有统计维度的 AjaxResult 字典
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:list')")
    @GetMapping("/stats")
    public AjaxResult getStats(@RequestParam(value = "month", required = false) String month) {
        // 1. 若前端没有传月份参数，则默认查询系统当前月份
        if (month == null || month.trim().isEmpty()) {
            month = new SimpleDateFormat("yyyy-MM").format(new Date());
        }

        // 2. 根据指定的月份，从数据库中拉取这个月所有的费用记录明细
        PmsFeeRecord query = new PmsFeeRecord();
        query.setFeeMonth(month);
        List<PmsFeeRecord> records = pmsFeeRecordService.selectPmsFeeRecordList(query);

        // 3. 初始化各项统计数据的累加器
        BigDecimal totalPayable = BigDecimal.ZERO; // 本月总应收金额
        BigDecimal totalPaid = BigDecimal.ZERO;    // 本月总实收金额
        Set<Long> unpaidOwners = new HashSet<>();  // 欠费业主ID集合（利用Set自动去重功能，统计究竟有多少户欠费）

        int todayPaidCount = 0;                    // 今日收款笔数
        BigDecimal todayPaidAmount = BigDecimal.ZERO; // 今日收款总额

        // 格式化出今天的日期字符串（例如 "20260608"），用来过滤"今日收款"的数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todayStr = sdf.format(new Date());

        // 4. 开始遍历这个月的所有账单，并执行累加计算
        for (PmsFeeRecord r : records) {
            // 累加：所有账单的应收金额
            if (r.getFeeAmount() != null) {
                totalPayable = totalPayable.add(r.getFeeAmount());
            }
            // 累加：所有账单中已经收到的实收金额
            if (r.getPaidAmount() != null) {
                totalPaid = totalPaid.add(r.getPaidAmount());
            }
            // 状态判断：如果支付状态是 "0"(未缴费) 或 "2"(部分缴费)
            if ("0".equals(r.getPayStatus()) || "2".equals(r.getPayStatus())) {
                // 将欠费的业主ID放入集合中，用于后续统计总欠费户数
                unpaidOwners.add(r.getOwnerId());
            }

            // 判断今日收款记录：如果这笔账单有付款时间，且付款时间就是今天
            if (r.getPayTime() != null && todayStr.equals(sdf.format(r.getPayTime()))) {
                todayPaidCount++; // 今日收款笔数 +1
                if (r.getPaidAmount() != null) {
                    // 今日收款金额累加
                    todayPaidAmount = todayPaidAmount.add(r.getPaidAmount());
                }
            }
        }

        // 5. 计算剩余待收金额 = 总应收 - 总实收
        BigDecimal totalUnpaid = totalPayable.subtract(totalPaid);
        // 如果数据异常导致待收金额为负，兜底重置为0
        if (totalUnpaid.compareTo(BigDecimal.ZERO) < 0) {
            totalUnpaid = BigDecimal.ZERO;
        }

        // 6. 计算本月资金收缴率（实收 / 应收）
        BigDecimal payRate = BigDecimal.ZERO;
        if (totalPayable.compareTo(BigDecimal.ZERO) > 0) {
            // 乘以100转为百分比，保留1位小数，四舍五入
            payRate = totalPaid.multiply(new BigDecimal("100"))
                               .divide(totalPayable, 1, RoundingMode.HALF_UP);
        }

        // 7. 将计算出来的各个指标封装回响应对象，给前端画图表使用
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

    /**
     * 核心业务：一键自动生成月度物业费账单
     * 遍历所有处于“已售”状态的房屋，根据房屋面积和物业费单价自动计算出金额，并插入到费用表中
     * 
     * @param month 目标生成月份（格式 YYYY-MM）
     * @return 成功生成了几户房屋的账单信息
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:add')")
    @Log(title = "费用记录", businessType = BusinessType.INSERT)
    @PostMapping("/generateMonthly")
    public AjaxResult generateMonthly(@RequestParam("month") String month) {
        // 1. 严格校验传入月份的格式
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            return AjaxResult.error("月份格式错误，应为 YYYY-MM");
        }

        // 2. 拉取需要生成账单的房屋列表：首先查询状态为 "2"（已售）的房子
        PmsRoom roomQuery = new PmsRoom();
        roomQuery.setStatus("2");
        List<PmsRoom> rooms = pmsRoomService.selectPmsRoomList(roomQuery);
        // 兜底设计：如果系统里一套已售房子都没查到（可能是状态维护不及时），那就拉取系统里所有的房屋进行生成
        if (rooms.isEmpty()) {
            roomQuery.setStatus(null);
            rooms = pmsRoomService.selectPmsRoomList(roomQuery);
        }

        // 3. 从字典表中查询“物业管理费（WYLF）”的单价（元/平方米）
        PmsFeeType feeTypeQuery = new PmsFeeType();
        feeTypeQuery.setTypeCode("WYLF");
        List<PmsFeeType> feeTypes = pmsFeeTypeService.selectPmsFeeTypeList(feeTypeQuery);
        PmsFeeType feeType = (feeTypes != null && !feeTypes.isEmpty()) ? feeTypes.get(0) : null;
        
        // 如果连计费单价都没配，直接阻断生成过程，提示用户去基础配置里配置
        if (feeType == null) {
            return AjaxResult.error("未找到物业管理费费用类型，请先在费用类型管理中配置");
        }

        // 成功生成的计数器
        int generatedCount = 0;

        // 4. 遍历所有找出的房屋，挨个计算费用并插入记录
        for (PmsRoom room : rooms) {
            // 防重复判断：查询该房屋在当前月是否已经生成过这个费用的账单
            PmsFeeRecord recordQuery = new PmsFeeRecord();
            recordQuery.setRoomId(room.getRoomId());
            recordQuery.setFeeMonth(month);
            recordQuery.setFeeTypeId(feeType.getFeeTypeId());
            List<PmsFeeRecord> existing = pmsFeeRecordService.selectPmsFeeRecordList(recordQuery);

            // 如果该房子这个月的物业费账单已经存在，跳过，不重复生成（幂等性保护）
            if (existing != null && !existing.isEmpty()) {
                continue;
            }

            // 初始化要插入的账单对象
            PmsFeeRecord record = new PmsFeeRecord();
            Long communityId = null;
            
            // 数据级联：通过房屋绑定的楼宇ID，往上查找到它所属的小区ID
            if (room.getBuildingId() != null) {
                PmsBuilding building = pmsBuildingService.selectPmsBuildingByBuildingId(room.getBuildingId());
                if (building != null && building.getCommunityId() != null) {
                    communityId = building.getCommunityId();
                }
            }

            record.setCommunityId(communityId);
            record.setRoomId(room.getRoomId());

            // 智能溯源：查找这个房子以前交物业费时关联的是哪个业主ID
            // 因为当前的表结构设计没有把房间和业主做直接的强绑定（通过历史记录反向推导是最安全的过渡方案）
            Long ownerId = null;
            PmsFeeRecord historyQuery = new PmsFeeRecord();
            historyQuery.setRoomId(room.getRoomId());
            List<PmsFeeRecord> histories = pmsFeeRecordService.selectPmsFeeRecordList(historyQuery);
            if (histories != null && !histories.isEmpty()) {
                // 取历史记录里出现的第一个非空业主作为本次账单的欠费业主
                for (PmsFeeRecord h : histories) {
                    if (h.getOwnerId() != null) {
                        ownerId = h.getOwnerId();
                        break;
                    }
                }
            }

            record.setOwnerId(ownerId);
            record.setFeeTypeId(feeType.getFeeTypeId());

            // 核心金额计算：房屋建筑面积 * 单价
            BigDecimal area = room.getConstructionArea();
            if (area == null || area.compareTo(BigDecimal.ZERO) <= 0) {
                // 如果该房子没有登记面积，或者面积异常（<=0），无法计费，直接跳过
                continue;
            }
            // 乘法运算后保留2位小数，四舍五入
            BigDecimal amount = area.multiply(feeType.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);

            record.setFeeAmount(amount);           // 应收金额
            record.setPaidAmount(BigDecimal.ZERO); // 实收初始为0
            record.setFeeMonth(month);
            record.setPayStatus("0");              // 状态设为"0-未交"
            record.setCreateBy(getUsername());

            // 执行插入数据库
            pmsFeeRecordService.insertPmsFeeRecord(record);
            generatedCount++; // 成功计数+1
        }

        // 返回批量生成的结果报告
        return AjaxResult.success("成功为 " + generatedCount + " 户房屋生成 " + month + " 月度账单");
    }

    /**
     * 一键催缴提醒接口
     * 根据指定的月份，拉出所有处于"未交"状态的账单，并可以配合第三方短信或微信通知渠道下发消息
     * （当前版本未接第三方接口，仅演示返回待催缴名单日志）
     * 
     * @param month 要催缴的月份
     * @return 包含虚拟发送日志的成功响应
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:edit')")
    @PostMapping("/remindAll")
    public AjaxResult remindAll(@RequestParam("month") String month) {
        // 1. 查询该月份下所有状态为 "0"(未交) 的费用记录
        PmsFeeRecord query = new PmsFeeRecord();
        query.setFeeMonth(month);
        query.setPayStatus("0");
        List<PmsFeeRecord> unpaidRecords = pmsFeeRecordService.selectPmsFeeRecordList(query);

        int count = 0;
        // 存放催缴结果日志给前端显示用
        List<String> logs = new ArrayList<>();

        // 2. 遍历欠费账单
        for (PmsFeeRecord r : unpaidRecords) {
            String ownerName = r.getOwnerName();
            // 如果账单里没存名字，但是有业主ID，去业主表查一查名字补上
            if (r.getOwnerId() != null) {
                PmsOwner owner = pmsOwnerService.selectPmsOwnerByOwnerId(r.getOwnerId());
                if (owner != null && ownerName == null) {
                    ownerName = owner.getOwnerName();
                }
            }
            // 如果还是没有，显示缺省的"业主"
            if (ownerName == null) {
                ownerName = "业主";
            }

            // 此处可编写调用短信网关发送短信的代码： SMSUtil.send(phone, "您好，您...未交");
            // 当前仅仅是生成一条执行日志
            logs.add("待催缴: " + ownerName + " - " + month + "月物业费 ¥" + r.getFeeAmount());
            count++;
        }

        // 3. 将总数量和日志打包返回前端弹窗
        AjaxResult ajax = AjaxResult.success("批量催缴提醒发送完成，共 " + count + " 户");
        ajax.put("logs", logs);
        return ajax;
    }

    /**
     * 单笔账单快捷收款确认
     * 保安或财务在核对后，点击"收款"按钮调用此接口，一键将状态流转为已收
     * 
     * @param recordId 需要确认收款的账单主键
     * @return 确认成功的结果
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:edit')")
    @Log(title = "费用记录", businessType = BusinessType.UPDATE)
    @PostMapping("/payQuick")
    public AjaxResult payQuick(@RequestParam("recordId") Long recordId) {
        // 1. 查找此单
        PmsFeeRecord record = pmsFeeRecordService.selectPmsFeeRecordByRecordId(recordId);
        if (record == null) {
            return AjaxResult.error("账单记录不存在");
        }

        // 2. 修改业务关键字段：状态"1"(已结清)，实收等于应收，付款时间为当前时刻
        record.setPayStatus("1");
        record.setPaidAmount(record.getFeeAmount());
        record.setPayTime(new Date());
        record.setUpdateBy(getUsername());

        // 3. 保存入库
        return toAjax(pmsFeeRecordService.updatePmsFeeRecord(record));
    }

    /**
     * 批量收款确认接口
     * 前端勾选多笔待缴账单，点击顶部"批量收款"按钮后触发
     * 
     * @param recordIds 前端传递过来的已勾选的多个账单ID数组
     * @return 成功操作的条数结果
     */
    @PreAuthorize("@ss.hasPermi('property:feeRecord:edit')")
    @Log(title = "费用记录", businessType = BusinessType.UPDATE)
    @PostMapping("/payBatch")
    public AjaxResult payBatch(@RequestParam("recordIds") Long[] recordIds) {
        // 空校验保护
        if (recordIds == null || recordIds.length == 0) {
            return AjaxResult.error("请选择要收款的账单记录");
        }
        
        int count = 0;
        // 遍历处理选中的每一个ID
        for (Long id : recordIds) {
            PmsFeeRecord record = pmsFeeRecordService.selectPmsFeeRecordByRecordId(id);
            // 只处理存在且状态不是已结清的账单
            if (record != null && !"1".equals(record.getPayStatus())) {
                record.setPayStatus("1");
                record.setPaidAmount(record.getFeeAmount());
                record.setPayTime(new Date());
                record.setUpdateBy(getUsername());
                // 同步至数据库
                pmsFeeRecordService.updatePmsFeeRecord(record);
                count++;
            }
        }
        return AjaxResult.success("批量收款成功，已确认 " + count + " 笔账单");
    }
}
