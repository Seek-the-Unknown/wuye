package com.ruoyi.property.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.property.domain.PmsFeeRecord;
import com.ruoyi.property.domain.PmsFeeType;
import com.ruoyi.property.domain.PmsParking;
import com.ruoyi.property.domain.PmsVehicleRecord;
import com.ruoyi.property.mapper.PmsFeeRecordMapper;
import com.ruoyi.property.mapper.PmsFeeTypeMapper;
import com.ruoyi.property.mapper.PmsParkingMapper;
import com.ruoyi.property.mapper.PmsVehicleRecordMapper;
import com.ruoyi.property.service.IPmsVehicleRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 车辆进出记录Service业务层处理
 * 这是整个停车场模块最核心的实现类，包含复杂的车辆入场判定、出场计费、与 Python 深度学习模型接口的通信等逻辑
 */
@Service
public class PmsVehicleRecordServiceImpl implements IPmsVehicleRecordService {

    private static final Logger log = LoggerFactory.getLogger(PmsVehicleRecordServiceImpl.class);

    @Autowired
    private PmsVehicleRecordMapper pmsVehicleRecordMapper;

    @Autowired
    private PmsParkingMapper pmsParkingMapper;

    @Autowired
    private PmsFeeTypeMapper pmsFeeTypeMapper;

    @Autowired
    private PmsFeeRecordMapper pmsFeeRecordMapper;

    /** 用于发送 HTTP 请求给外部 Python 微服务 */
    @Autowired
    private RestTemplate restTemplate;

    /** 从 application.yml 中读取的 Python 车牌识别服务地址 */
    @Value("${lpr.service-url:http://localhost:8000}")
    private String lprServiceUrl;

    /** 从 application.yml 中读取的车牌识别置信度阈值（低于此值报警） */
    @Value("${lpr.min-confidence:0.7}")
    private double minConfidence;

    /** JSON 序列化工具 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 查询车辆进出记录详情
     */
    @Override
    public PmsVehicleRecord selectPmsVehicleRecordByRecordId(Long recordId) {
        return pmsVehicleRecordMapper.selectPmsVehicleRecordByRecordId(recordId);
    }

    /**
     * 根据条件查询记录列表
     */
    @Override
    public List<PmsVehicleRecord> selectPmsVehicleRecordList(PmsVehicleRecord record) {
        return pmsVehicleRecordMapper.selectPmsVehicleRecordList(record);
    }

    /**
     * 基础新增（仅记录时间插入，不包含业务逻辑）
     */
    @Override
    public int insertPmsVehicleRecord(PmsVehicleRecord record) {
        record.setCreateTime(DateUtils.getNowDate());
        return pmsVehicleRecordMapper.insertPmsVehicleRecord(record);
    }

    /**
     * 基础更新
     */
    @Override
    public int updatePmsVehicleRecord(PmsVehicleRecord record) {
        record.setUpdateTime(DateUtils.getNowDate());
        return pmsVehicleRecordMapper.updatePmsVehicleRecord(record);
    }

    /**
     * 基础批量删除
     */
    @Override
    public int deletePmsVehicleRecordByRecordIds(Long[] recordIds) {
        return pmsVehicleRecordMapper.deletePmsVehicleRecordByRecordIds(recordIds);
    }

    /**
     * 基础单条删除
     */
    @Override
    public int deletePmsVehicleRecordByRecordId(Long recordId) {
        return pmsVehicleRecordMapper.deletePmsVehicleRecordByRecordId(recordId);
    }

    /**
     * 依据车牌号查询该车最后一条处于"在场"（pay_status = '0'）状态的入场记录
     */
    @Override
    public PmsVehicleRecord selectActiveRecordByPlate(String plateNumber) {
        return pmsVehicleRecordMapper.selectActiveRecordByPlate(plateNumber);
    }

    /**
     * 核心业务：车辆入场处理
     * @param file 车辆照片
     * @param communityId 小区ID
     * @param parkingId 关联车位ID
     * @param plateNumber 手动输入的车牌号
     * @param manualVehicleType 手动指定的车辆类型（覆盖自动判定）
     * @return 刚创建的车辆入场记录
     */
    @Override
    @Transactional
    public PmsVehicleRecord handleVehicleEnter(MultipartFile file, Long communityId, Long parkingId, String plateNumber, String manualVehicleType) {
        // 1. 解析获得最终车牌号（若传了照片就调Python识别，若传了文字就用文字）
        String plate = resolvePlate(file, plateNumber);
        // 2. 将解析好的车牌号、类型及车位等信息封装写入数据库，生成入场账单记录
        return createEntryRecord(plate, communityId, parkingId, manualVehicleType);
    }

    /**
     * 核心业务：车辆出场处理
     * @param file 车辆照片
     * @param plateNumber 手动输入的车牌号
     * @return 结算完毕的车辆出场记录
     */
    @Override
    @Transactional
    public PmsVehicleRecord handleVehicleExit(MultipartFile file, String plateNumber) {
        // 1. 解析获得最终要出场的车牌号
        String plate = resolvePlate(file, plateNumber);
        // 2. 根据车牌号找到其入场记录，算出停车时长、扣费，并更新状态放行
        return processExit(plate);
    }

    /**
     * 辅助方法：解析出最终车牌号
     * 逻辑规则：如果有手动输入的车牌优先使用手动的（允许保安纠错）；如果没有，就调用图像识别服务。
     */
    private String resolvePlate(MultipartFile file, String manualPlate) {
        boolean hasFile = file != null && !file.isEmpty();
        boolean hasPlate = manualPlate != null && !manualPlate.trim().isEmpty();
        // 1. 优先采用人工输入的车牌，以人工判断为准
        if (hasPlate) return manualPlate.trim();
        // 2. 若没有人工输入但有图片，则调用外部 Python AI 模型进行图像智能识别
        if (hasFile) return recognizePlate(file);
        // 3. 都没有则抛出异常阻止业务
        throw new ServiceException("请上传车辆照片或手动输入车牌号");
    }

    /**
     * 核心流程步骤 1：创建入场记录，关联停车位，判断车辆身份
     */
    private PmsVehicleRecord createEntryRecord(String plateNumber, Long communityId, Long parkingId, String manualVehicleType) {
        // 1. 查停车位字典：看看系统里有没有业主把这个车牌绑定到了自己的专属车位上
        PmsParking parking = pmsParkingMapper.selectPmsParkingByPlateNumber(plateNumber);

        Long actualParkingId = parkingId;
        Long actualCommunityId = communityId;
        
        // 2. 默认车辆身份判定：默认设定为"临时车(0)"
        String vehicleType = "0"; 

        // 3. 如果在车位表里查到了该车牌的登记记录，说明它是内部车辆
        if (parking != null) {
            // 获取绑定的车位ID和所属小区
            if (actualParkingId == null) actualParkingId = parking.getParkingId();
            if (actualCommunityId == null) actualCommunityId = parking.getCommunityId();

            // 权限升级判定：如果这个车位绑定了具体的业主，这辆车就是"业主车(2)"；
            // 如果仅登记了车位但没绑定业主（比如租客/月卡），算作"月租车(1)"
            vehicleType = parking.getOwnerId() != null ? "2" : "1";

            // 如果查到的车位当前是空闲状态(0)，将其状态置为被占用(1)
            if ("0".equals(parking.getBindStatus())) {
                parking.setBindStatus("1");
                parking.setUpdateTime(DateUtils.getNowDate());
                pmsParkingMapper.updatePmsParking(parking);
            }
        }
        
        // 4. 用户手动干预：如果保安在前端页面手动下拉选择了类型（比如遇到识别错误或者家属车情况），强制覆盖系统判定的类型
        if (manualVehicleType != null && !manualVehicleType.isEmpty()) {
            vehicleType = manualVehicleType;
        }

        // 5. 组装入场记录对象
        PmsVehicleRecord record = new PmsVehicleRecord();
        record.setPlateNumber(plateNumber);
        record.setCommunityId(actualCommunityId);
        record.setParkingId(actualParkingId);
        record.setEntryTime(new Date()); // 入场时间戳就是现在
        record.setPayStatus("0"); // 标记为：0-在场未缴费
        record.setVehicleType(vehicleType);
        record.setCreateTime(DateUtils.getNowDate());

        // 6. 截取当前的停车单价配置，作为该车辆本次停车的计费依据（防止未来单价变更导致老记录价格混乱，即金额快照）
        BigDecimal rate = getParkingHourlyRate();
        record.setUnitPrice(rate);

        // 7. 入库持久化
        pmsVehicleRecordMapper.insertPmsVehicleRecord(record);
        log.info("Vehicle entered: plate={}, vehicleType={}, parkingId={}, rate={}/h",
            plateNumber, vehicleType, actualParkingId, rate);
            
        return record;
    }

    /**
     * 核心流程步骤 2：完成出场（计算时间差、计算金额、生成财务收费流水）
     */
    private PmsVehicleRecord processExit(String plateNumber) {
        // 1. 查询该车牌在库中状态为"在场"的记录
        PmsVehicleRecord record = pmsVehicleRecordMapper.selectActiveRecordByPlate(plateNumber);
        // 如果查不到入场记录（比如保安之前没录），没法算时间，必须报错让人工处理
        if (record == null) {
            throw new ServiceException("未找到车牌号【" + plateNumber + "】的入场记录，请手动处理");
        }

        // 2. 算停车时间
        Date exitTime = new Date();
        // 计算"出场时瞬间"与"入场时间"之间相差多少时长对象
        Duration duration = Duration.between(record.getEntryTime().toInstant(), exitTime.toInstant());
        long minutes = duration.toMinutes();
        // 不足一小时按一小时计算，例如 61分钟算 2小时。(+59进位法)
        long hours = Math.max(1, (minutes + 59) / 60);

        // 3. 计算最终需缴纳的停车费
        BigDecimal parkingDuration = BigDecimal.valueOf(hours);
        // 取出入场时系统记录下来的单价（如果入场时没记上，现在再查一遍字典）
        BigDecimal unitPrice = record.getUnitPrice() != null ? record.getUnitPrice() : getParkingHourlyRate();
        // 停车总金额 = 时长(小时) * 单价(元/小时)
        BigDecimal feeAmount = unitPrice.multiply(parkingDuration).setScale(2, RoundingMode.HALF_UP);

        // 4. 更新车辆的出场数据字段
        record.setExitTime(exitTime);
        record.setParkingDuration(parkingDuration); // 存入算出的小时数
        record.setUnitPrice(unitPrice);
        record.setFeeAmount(feeAmount);             // 应付停车费
        record.setPaidAmount(feeAmount);            // 假设出场即交清，实付 = 应付（此处简化了扫码支付流程）
        record.setPayStatus("1");                   // 标记为：1-已离场(或已缴费)
        record.setPayTime(exitTime);                // 缴费时间记录当前
        record.setUpdateTime(DateUtils.getNowDate());
        // 落库
        pmsVehicleRecordMapper.updatePmsVehicleRecord(record);

        // 5. 车辆开走后，如果它停在了某个固定车位上，要把该车位状态还原为"空闲"
        if (record.getParkingId() != null) {
            PmsParking parking = pmsParkingMapper.selectPmsParkingByParkingId(record.getParkingId());
            if (parking != null && "1".equals(parking.getBindStatus())) {
                parking.setBindStatus("0"); // 置0释放资源
                parking.setUpdateTime(DateUtils.getNowDate());
                pmsParkingMapper.updatePmsParking(parking);
                log.info("Parking space {} released", parking.getParkingCode());
            }
        }

        // 6. 同步写入到物业财务总控表的流水账单（pms_fee_record）中，方便出纳统一对账
        createFeeRecord(record, unitPrice, feeAmount, exitTime);

        log.info("Vehicle exited: plate={}, duration={}h, fee={}", plateNumber, parkingDuration, feeAmount);
        return record;
    }

    /**
     * 辅助方法：生成全局财务系统的费用流水记录
     */
    private void createFeeRecord(PmsVehicleRecord record, BigDecimal unitPrice, BigDecimal feeAmount, Date payTime) {
        // 先去字典查停车费这种收费类型对应的 ID 
        Long feeTypeId = getParkingFeeTypeId();
        // 如果系统没配置"停车费"这个科目，就跳过不记录流水
        if (feeTypeId == null) return;

        // 提取年月用于财务统计归档
        String feeMonth = new SimpleDateFormat("yyyy-MM").format(payTime);

        // 创建新的财务收费对象
        PmsFeeRecord feeRecord = new PmsFeeRecord();
        feeRecord.setCommunityId(record.getCommunityId());
        feeRecord.setFeeTypeId(feeTypeId);
        feeRecord.setFeeAmount(feeAmount); // 应付等于传进来的最终金额
        feeRecord.setPaidAmount(feeAmount);// 实付等于应付（默认全部收齐）
        feeRecord.setFeeMonth(feeMonth);
        feeRecord.setPayStatus("1");       // 直接标记为已缴清
        feeRecord.setPayTime(payTime);
        feeRecord.setCreateBy("system");   // 系统自动代付生成的
        feeRecord.setCreateTime(DateUtils.getNowDate());
        feeRecord.setRemark("车牌号: " + record.getPlateNumber()
            + "，停车时长: " + record.getParkingDuration() + "小时"
            + "，单价: " + unitPrice + "元/小时");

        // 如果这辆车有关联固定车位，尝试找车位主人，把这笔费用挂到他名下（方便后续按户主查账）
        if (record.getParkingId() != null) {
            PmsParking parking = pmsParkingMapper.selectPmsParkingByParkingId(record.getParkingId());
            if (parking != null && parking.getOwnerId() != null) {
                feeRecord.setOwnerId(parking.getOwnerId());
            }
        }

        // 写入财务表
        pmsFeeRecordMapper.insertPmsFeeRecord(feeRecord);
        log.info("Fee record created: plate={}, amount={}, feeMonth={}", record.getPlateNumber(), feeAmount, feeMonth);
    }

    /**
     * 辅助方法：去数据库查"停车费(PARKING)"的单价
     */
    private BigDecimal getParkingHourlyRate() {
        PmsFeeType query = new PmsFeeType();
        query.setTypeCode("PARKING");
        List<PmsFeeType> types = pmsFeeTypeMapper.selectPmsFeeTypeList(query);
        // 如果查到了就在列表中取第一个
        if (types != null && !types.isEmpty()) {
            BigDecimal price = types.get(0).getUnitPrice();
            return price != null ? price : BigDecimal.valueOf(5); // 配置了类型但没写金额，给个保底5元
        }
        // 如果啥也没查到，直接返回保底5元（防爆设计）
        return BigDecimal.valueOf(5);
    }

    /**
     * 辅助方法：去数据库查"停车费(PARKING)"的科目ID
     */
    private Long getParkingFeeTypeId() {
        PmsFeeType query = new PmsFeeType();
        query.setTypeCode("PARKING");
        List<PmsFeeType> types = pmsFeeTypeMapper.selectPmsFeeTypeList(query);
        return (types != null && !types.isEmpty()) ? types.get(0).getFeeTypeId() : null;
    }

    /**
     * 核心业务：跨语言远程调用 Python (HyperLPR3) 车牌识别微服务
     * 此过程通过 HTTP POST Form-Data 形式将图片上传过去，接受返回结果 JSON
     * 
     * @param file 前端传入的照片文件对象
     * @return 解析后的车牌字符串（例如 "京N8P8F8"）
     */
    @SuppressWarnings("unchecked")
    private String recognizePlate(MultipartFile file) {
        try {
            // 1. 组装 HTTP 请求头，声明我要传的是多部分表单类型（即文件上传格式）
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 2. 组装请求体，将 MultipartFile 转成 Resource 塞入 body 中
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            // 3. 将头部和请求体合并封装
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 4. 使用 RestTemplate 发送同步的 POST 请求至 Python 服务（地址通常为 http://localhost:8000/recognize）
            ResponseEntity<String> response = restTemplate.postForEntity(
                lprServiceUrl + "/recognize", requestEntity, String.class
            );

            // 5. 将 Python 端返回的 JSON 字符串反序列化成 Map
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);

            // 6. 校验 Python 端约定的 success 标志位
            Boolean success = (Boolean) result.get("success");
            if (success == null || !success) {
                // 不成功就提取报错信息并对外抛出
                String error = (String) result.getOrDefault("error", "车牌识别失败");
                throw new ServiceException(error + "，请重新拍照或手动输入");
            }

            // 7. 取出解析出来的核心数据——车牌号
            String plate = (String) result.get("plate_number");
            if (plate == null || plate.isEmpty()) {
                throw new ServiceException("未识别到车牌号，请重新拍照");
            }

            // 8. 取出 Python 模型给出的置信度（Confidence），代表模型觉得它有多大的把握认对了
            double confidence = 0;
            if (result.get("confidence") instanceof Number) {
                confidence = ((Number) result.get("confidence")).doubleValue();
            }

            log.info("Plate recognized: {} (confidence: {})", plate, confidence);

            // 9. 如果置信度低于我们在配置里写死的阈值（如0.7），虽放行但后台打一个警告日志，以便日后调优模型
            if (confidence < minConfidence) {
                log.warn("Low confidence recognition: {} ({} < {})", plate, confidence, minConfidence);
            }

            // 10. 大功告成，返回车牌文本
            return plate;

        } catch (RestClientException e) {
            // 捕获网络通信异常（例如 Python 服务没启动、端口被墙等）
            log.error("Python LPR service unavailable: {}", e.getMessage());
            throw new ServiceException("车牌识别服务暂不可用，请联系管理员或手动输入车牌号");
        } catch (ServiceException e) {
            // 捕获自己在代码上文抛出的业务异常，原样往外抛
            throw e;
        } catch (Exception e) {
            // 兜底捕获一切未知错误（如 JSON 解析失败、空指针等），包装成统一服务异常
            log.error("Plate recognition error", e);
            throw new ServiceException("车牌识别过程出错: " + e.getMessage());
        }
    }
}
