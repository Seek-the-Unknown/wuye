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

/**
 * 车辆进出记录控制层
 * 主要负责接收前端的车辆入场、出场请求，并调用底层的 Python 车牌识别微服务进行计费和状态流转
 */
@RestController
@RequestMapping("/property/vehicleRecord")
public class PmsVehicleRecordController extends BaseController {

    /** 车辆进出记录的服务层接口，负责核心业务逻辑处理 */
    @Autowired
    private IPmsVehicleRecordService pmsVehicleRecordService;

    /** 费用类型的服务层接口，用于查询基础停车费单价 */
    @Autowired
    private IPmsFeeTypeService pmsFeeTypeService;

    /**
     * 查询车辆进出记录列表
     * @param pmsVehicleRecord 查询参数对象（包含车牌号、进出时间、状态等条件）
     * @return 包含分页数据的 TableDataInfo 对象
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsVehicleRecord pmsVehicleRecord) {
        // 1. 开启 Mybatis 分页插件
        startPage();
        // 2. 调用服务层，根据条件查询车辆进出记录
        List<PmsVehicleRecord> list = pmsVehicleRecordService.selectPmsVehicleRecordList(pmsVehicleRecord);
        // 3. 封装为前端需要的分页表格数据并返回
        return getDataTable(list);
    }

    /**
     * 根据主键ID获取某条具体的车辆进出记录详情
     * @param recordId 记录的主键ID
     * @return 包含车辆记录对象的标准 AjaxResult 响应
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId) {
        // 1. 根据 ID 查询单条记录并包裹成功响应返回
        return success(pmsVehicleRecordService.selectPmsVehicleRecordByRecordId(recordId));
    }

    /**
     * 后台手动新增一条车辆进出记录（通常用于管理员补录异常记录）
     * @param pmsVehicleRecord 前端传来的JSON对象
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:add')")
    @Log(title = "车辆进出记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsVehicleRecord pmsVehicleRecord) {
        // 1. 记录创建人（当前登录用户）
        pmsVehicleRecord.setCreateBy(getUsername());
        // 2. 插入数据库并返回受影响的行数，转为前端响应
        return toAjax(pmsVehicleRecordService.insertPmsVehicleRecord(pmsVehicleRecord));
    }

    /**
     * 后台手动修改某条车辆进出记录（比如管理员修改错误的计费状态等）
     * @param pmsVehicleRecord 前端传来的带有ID的JSON对象
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:edit')")
    @Log(title = "车辆进出记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsVehicleRecord pmsVehicleRecord) {
        // 1. 记录更新人（当前登录用户）
        pmsVehicleRecord.setUpdateBy(getUsername());
        // 2. 更新数据库并返回操作结果
        return toAjax(pmsVehicleRecordService.updatePmsVehicleRecord(pmsVehicleRecord));
    }

    /**
     * 批量或单条删除车辆进出记录
     * @param recordIds 需要删除的记录ID数组（通过URL路径传递）
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:remove')")
    @Log(title = "车辆进出记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        // 1. 物理删除记录
        return toAjax(pmsVehicleRecordService.deletePmsVehicleRecordByRecordIds(recordIds));
    }

    /**
     * 核心业务：车辆入场接口
     * 业务流程：接收照片/手动车牌 -> 识别车牌 -> 判断是否有绑定车位 -> 判断车辆类型(业主/月租/临时) -> 写入数据库，标记状态为"在场"(0)
     * 
     * @param file        前端传来的车辆照片文件（可选）
     * @param communityId 小区ID（可选）
     * @param parkingId   关联车位ID（可选）
     * @param plateNumber 手动输入的车牌号（可选，若有照片优先识别照片，否则使用手动输入的）
     * @param vehicleType 手动覆盖的车辆类型（0临时，1月租，2业主）
     * @return 包含处理后的记录对象的 AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:enter')")
    @Log(title = "车辆入场识别", businessType = BusinessType.INSERT)
    @PostMapping("/enter")
    public AjaxResult enter(@RequestParam(value = "file", required = false) MultipartFile file,
                            @RequestParam(value = "communityId", required = false) Long communityId,
                            @RequestParam(value = "parkingId", required = false) Long parkingId,
                            @RequestParam(value = "plateNumber", required = false) String plateNumber,
                            @RequestParam(value = "vehicleType", required = false) String vehicleType) {
        // 1. 基础校验：照片和手动输入车牌必须至少提供一个，否则无法入场
        if ((file == null || file.isEmpty()) && (plateNumber == null || plateNumber.trim().isEmpty())) {
            return AjaxResult.error("请上传车辆照片或手动输入车牌号");
        }
        
        // 2. 调用业务层的入场逻辑，其中包含了 Python 服务的远程调用（若传了文件）以及数据初始化
        PmsVehicleRecord record = pmsVehicleRecordService.handleVehicleEnter(file, communityId, parkingId, plateNumber, vehicleType);
        
        // 3. 返回包含新创建入场信息的成功响应
        return success(record);
    }

    /**
     * 核心业务：车辆出场接口
     * 业务流程：接收照片/手动车牌 -> 识别车牌 -> 查出库中属于该车牌且状态为"在场"的记录 -> 根据入场时间与单价计算费用 -> 更改状态为"已离场"(1) -> 创建收费明细
     * 
     * @param file        前端传来的车辆出场照片（可选）
     * @param plateNumber 手动输入的车牌号（可选）
     * @return 包含已结算的车辆记录（含费用信息）的 AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:exit')")
    @Log(title = "车辆出场计费", businessType = BusinessType.UPDATE)
    @PostMapping("/exit")
    public AjaxResult exit(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "plateNumber", required = false) String plateNumber) {
        
        // 1. 基础参数有效性校验
        boolean hasFile = file != null && !file.isEmpty();
        boolean hasPlate = plateNumber != null && !plateNumber.trim().isEmpty();
        
        // 2. 至少需要提供一种车辆身份标识依据
        if (!hasFile && !hasPlate) {
            return AjaxResult.error("请上传车辆照片或手动输入车牌号");
        }
        
        // 3. 委派给业务层进行车牌解析、计费计算、记录更新以及产生财务流水
        PmsVehicleRecord record = pmsVehicleRecordService.handleVehicleExit(file, plateNumber);
        
        // 4. 将结算后的信息（例如停车时长、应付金额等）返回给前端弹窗展示
        return success(record);
    }

    /**
     * 获取当前系统配置的停车计费单价
     * 用于前端出入场页面展示给保安人员查看当前计费标准
     * 会查询收费类型配置表(pms_fee_type)中类型代码为'PARKING'的单价
     */
    @PreAuthorize("@ss.hasPermi('property:vehicleRecord:list')")
    @GetMapping("/getHourlyRate")
    public AjaxResult getHourlyRate() {
        // 1. 构建查询条件：查询编码为 PARKING 的收费类型
        PmsFeeType query = new PmsFeeType();
        query.setTypeCode("PARKING");
        
        // 2. 执行查询，拿到所有匹配的费用配置
        List<PmsFeeType> types = pmsFeeTypeService.selectPmsFeeTypeList(query);
        
        // 3. 如果有查到，取第一条的单价返回给前端
        if (types != null && !types.isEmpty()) {
            PmsFeeType parking = types.get(0);
            return AjaxResult.success().put("unitPrice", parking.getUnitPrice());
        }
        
        // 4. 如果数据库里没有配置，为了系统不报错，提供一个默认单价 5 元/小时
        return AjaxResult.success().put("unitPrice", BigDecimal.valueOf(5));
    }

    /**
     * 按车牌号主动查询当前处于“在场”状态（pay_status = '0'）的入场记录
     * 用于前端验证某辆车是否已经在小区里，防止同一辆车重复入场或者在没有入场记录时点击出场
     * 
     * @param plateNumber 车牌号
     * @return 正在场内的唯一记录
     */
    @GetMapping("/searchActive")
    public AjaxResult searchActive(@RequestParam("plateNumber") String plateNumber) {
        // 1. 根据车牌调用服务层查询"在场"记录
        PmsVehicleRecord record = pmsVehicleRecordService.selectActiveRecordByPlate(plateNumber);
        
        // 2. 如果没找到，说明车不在场内，返回错误信息（前端据此阻止出场）
        if (record == null) {
            return AjaxResult.error("未找到车牌号【" + plateNumber + "】的入场记录");
        }
        
        // 3. 返回找到的记录数据
        return success(record);
    }
}
