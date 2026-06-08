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

    @Autowired
    private RestTemplate restTemplate;

    @Value("${lpr.service-url:http://localhost:8000}")
    private String lprServiceUrl;

    @Value("${lpr.min-confidence:0.7}")
    private double minConfidence;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PmsVehicleRecord selectPmsVehicleRecordByRecordId(Long recordId) {
        return pmsVehicleRecordMapper.selectPmsVehicleRecordByRecordId(recordId);
    }

    @Override
    public List<PmsVehicleRecord> selectPmsVehicleRecordList(PmsVehicleRecord record) {
        return pmsVehicleRecordMapper.selectPmsVehicleRecordList(record);
    }

    @Override
    public int insertPmsVehicleRecord(PmsVehicleRecord record) {
        record.setCreateTime(DateUtils.getNowDate());
        return pmsVehicleRecordMapper.insertPmsVehicleRecord(record);
    }

    @Override
    public int updatePmsVehicleRecord(PmsVehicleRecord record) {
        record.setUpdateTime(DateUtils.getNowDate());
        return pmsVehicleRecordMapper.updatePmsVehicleRecord(record);
    }

    @Override
    public int deletePmsVehicleRecordByRecordIds(Long[] recordIds) {
        return pmsVehicleRecordMapper.deletePmsVehicleRecordByRecordIds(recordIds);
    }

    @Override
    public int deletePmsVehicleRecordByRecordId(Long recordId) {
        return pmsVehicleRecordMapper.deletePmsVehicleRecordByRecordId(recordId);
    }

    @Override
    public PmsVehicleRecord selectActiveRecordByPlate(String plateNumber) {
        return pmsVehicleRecordMapper.selectActiveRecordByPlate(plateNumber);
    }

    @Override
    @Transactional
    public PmsVehicleRecord handleVehicleEnter(MultipartFile file, Long communityId, Long parkingId, String plateNumber) {
        String plate = resolvePlate(file, plateNumber);
        return createEntryRecord(plate, communityId, parkingId);
    }

    @Override
    @Transactional
    public PmsVehicleRecord handleVehicleExit(MultipartFile file, String plateNumber) {
        String plate = resolvePlate(file, plateNumber);
        return processExit(plate);
    }

    private String resolvePlate(MultipartFile file, String manualPlate) {
        boolean hasFile = file != null && !file.isEmpty();
        boolean hasPlate = manualPlate != null && !manualPlate.trim().isEmpty();
        if (hasPlate) return manualPlate.trim();
        if (hasFile) return recognizePlate(file);
        throw new ServiceException("请上传车辆照片或手动输入车牌号");
    }

    /**
     * 创建入场记录，关联停车位
     */
    private PmsVehicleRecord createEntryRecord(String plateNumber, Long communityId, Long parkingId) {
        // 1. 查停车位：是否绑定了该车牌
        PmsParking parking = pmsParkingMapper.selectPmsParkingByPlateNumber(plateNumber);

        Long actualParkingId = parkingId;
        Long actualCommunityId = communityId;
        String vehicleType = "0"; // 默认临时车

        if (parking != null) {
            if (actualParkingId == null) actualParkingId = parking.getParkingId();
            if (actualCommunityId == null) actualCommunityId = parking.getCommunityId();

            // 有业主绑定 → 业主车；有车牌绑定但无业主 → 月租车
            vehicleType = parking.getOwnerId() != null ? "2" : "1";

            // 占用车位
            if ("0".equals(parking.getBindStatus())) {
                parking.setBindStatus("1");
                parking.setUpdateTime(DateUtils.getNowDate());
                pmsParkingMapper.updatePmsParking(parking);
            }
        }

        // 2. 创建入场记录
        PmsVehicleRecord record = new PmsVehicleRecord();
        record.setPlateNumber(plateNumber);
        record.setCommunityId(actualCommunityId);
        record.setParkingId(actualParkingId);
        record.setEntryTime(new Date());
        record.setPayStatus("0");
        record.setVehicleType(vehicleType);
        record.setCreateTime(DateUtils.getNowDate());

        // 读取当前停车单价到记录中（快照）
        BigDecimal rate = getParkingHourlyRate();
        record.setUnitPrice(rate);

        pmsVehicleRecordMapper.insertPmsVehicleRecord(record);
        log.info("Vehicle entered: plate={}, vehicleType={}, parkingId={}, rate={}/h",
            plateNumber, vehicleType, actualParkingId, rate);
        return record;
    }

    /**
     * 完成出场：计费 + 创建费用记录 + 释放车位
     */
    private PmsVehicleRecord processExit(String plateNumber) {
        PmsVehicleRecord record = pmsVehicleRecordMapper.selectActiveRecordByPlate(plateNumber);
        if (record == null) {
            throw new ServiceException("未找到车牌号【" + plateNumber + "】的入场记录，请手动处理");
        }

        Date exitTime = new Date();
        Duration duration = Duration.between(record.getEntryTime().toInstant(), exitTime.toInstant());
        long minutes = duration.toMinutes();
        long hours = Math.max(1, (minutes + 59) / 60);

        BigDecimal parkingDuration = BigDecimal.valueOf(hours);
        BigDecimal unitPrice = record.getUnitPrice() != null ? record.getUnitPrice() : getParkingHourlyRate();
        BigDecimal feeAmount = unitPrice.multiply(parkingDuration).setScale(2, RoundingMode.HALF_UP);

        // 更新车辆记录
        record.setExitTime(exitTime);
        record.setParkingDuration(parkingDuration);
        record.setUnitPrice(unitPrice);
        record.setFeeAmount(feeAmount);
        record.setPaidAmount(feeAmount);
        record.setPayStatus("1");
        record.setPayTime(exitTime);
        record.setUpdateTime(DateUtils.getNowDate());
        pmsVehicleRecordMapper.updatePmsVehicleRecord(record);

        // 释放车位
        if (record.getParkingId() != null) {
            PmsParking parking = pmsParkingMapper.selectPmsParkingByParkingId(record.getParkingId());
            if (parking != null && "1".equals(parking.getBindStatus())) {
                parking.setBindStatus("0");
                parking.setUpdateTime(DateUtils.getNowDate());
                pmsParkingMapper.updatePmsParking(parking);
                log.info("Parking space {} released", parking.getParkingCode());
            }
        }

        // 创建费用记录
        createFeeRecord(record, unitPrice, feeAmount, exitTime);

        log.info("Vehicle exited: plate={}, duration={}h, fee={}", plateNumber, parkingDuration, feeAmount);
        return record;
    }

    /**
     * 同步写一条费用记录到 pms_fee_record，与费用管理模块对接
     */
    private void createFeeRecord(PmsVehicleRecord record, BigDecimal unitPrice, BigDecimal feeAmount, Date payTime) {
        Long feeTypeId = getParkingFeeTypeId();
        if (feeTypeId == null) return;

        String feeMonth = new SimpleDateFormat("yyyy-MM").format(payTime);

        PmsFeeRecord feeRecord = new PmsFeeRecord();
        feeRecord.setCommunityId(record.getCommunityId());
        feeRecord.setFeeTypeId(feeTypeId);
        feeRecord.setFeeAmount(feeAmount);
        feeRecord.setPaidAmount(feeAmount);
        feeRecord.setFeeMonth(feeMonth);
        feeRecord.setPayStatus("1"); // 直接已缴
        feeRecord.setPayTime(payTime);
        feeRecord.setCreateBy("system");
        feeRecord.setCreateTime(DateUtils.getNowDate());
        feeRecord.setRemark("车牌号: " + record.getPlateNumber()
            + "，停车时长: " + record.getParkingDuration() + "小时"
            + "，单价: " + unitPrice + "元/小时");

        // 如果有业主绑定则关联业主
        if (record.getParkingId() != null) {
            PmsParking parking = pmsParkingMapper.selectPmsParkingByParkingId(record.getParkingId());
            if (parking != null && parking.getOwnerId() != null) {
                feeRecord.setOwnerId(parking.getOwnerId());
            }
        }

        pmsFeeRecordMapper.insertPmsFeeRecord(feeRecord);
        log.info("Fee record created: plate={}, amount={}, feeMonth={}", record.getPlateNumber(), feeAmount, feeMonth);
    }

    private BigDecimal getParkingHourlyRate() {
        PmsFeeType query = new PmsFeeType();
        query.setTypeCode("PARKING");
        List<PmsFeeType> types = pmsFeeTypeMapper.selectPmsFeeTypeList(query);
        if (types != null && !types.isEmpty()) {
            BigDecimal price = types.get(0).getUnitPrice();
            return price != null ? price : BigDecimal.valueOf(5);
        }
        return BigDecimal.valueOf(5);
    }

    private Long getParkingFeeTypeId() {
        PmsFeeType query = new PmsFeeType();
        query.setTypeCode("PARKING");
        List<PmsFeeType> types = pmsFeeTypeMapper.selectPmsFeeTypeList(query);
        return (types != null && !types.isEmpty()) ? types.get(0).getFeeTypeId() : null;
    }

    /**
     * 调用 Python 车牌识别服务
     */
    @SuppressWarnings("unchecked")
    private String recognizePlate(MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                lprServiceUrl + "/recognize", requestEntity, String.class
            );

            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);

            Boolean success = (Boolean) result.get("success");
            if (success == null || !success) {
                String error = (String) result.getOrDefault("error", "车牌识别失败");
                throw new ServiceException(error + "，请重新拍照或手动输入");
            }

            String plate = (String) result.get("plate_number");
            if (plate == null || plate.isEmpty()) {
                throw new ServiceException("未识别到车牌号，请重新拍照");
            }

            double confidence = 0;
            if (result.get("confidence") instanceof Number) {
                confidence = ((Number) result.get("confidence")).doubleValue();
            }

            log.info("Plate recognized: {} (confidence: {})", plate, confidence);

            if (confidence < minConfidence) {
                log.warn("Low confidence recognition: {} ({} < {})", plate, confidence, minConfidence);
            }

            return plate;

        } catch (RestClientException e) {
            log.error("Python LPR service unavailable: {}", e.getMessage());
            throw new ServiceException("车牌识别服务暂不可用，请联系管理员或手动输入车牌号");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Plate recognition error", e);
            throw new ServiceException("车牌识别过程出错: " + e.getMessage());
        }
    }
}
