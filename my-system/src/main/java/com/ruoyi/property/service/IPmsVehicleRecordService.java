package com.ruoyi.property.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.property.domain.PmsVehicleRecord;

/**
 * 车辆进出记录Service接口
 * 
 * @author ruoyi
 */
public interface IPmsVehicleRecordService {
    /**
     * 查询车辆进出记录
     * 
     * @param recordId 记录主键
     * @return 车辆进出记录
     */
    PmsVehicleRecord selectPmsVehicleRecordByRecordId(Long recordId);

    /**
     * 查询车辆进出记录列表
     * 
     * @param record 车辆进出记录
     * @return 车辆进出记录集合
     */
    List<PmsVehicleRecord> selectPmsVehicleRecordList(PmsVehicleRecord record);

    /**
     * 新增车辆进出记录
     * 
     * @param record 车辆进出记录
     * @return 结果
     */
    int insertPmsVehicleRecord(PmsVehicleRecord record);

    /**
     * 修改车辆进出记录
     * 
     * @param record 车辆进出记录
     * @return 结果
     */
    int updatePmsVehicleRecord(PmsVehicleRecord record);

    /**
     * 批量删除车辆进出记录
     * 
     * @param recordIds 需要删除的记录主键集合
     * @return 结果
     */
    int deletePmsVehicleRecordByRecordIds(Long[] recordIds);

    /**
     * 删除车辆进出记录信息
     * 
     * @param recordId 记录主键
     * @return 结果
     */
    int deletePmsVehicleRecordByRecordId(Long recordId);

    /**
     * 根据车牌号查询最近的一条活跃（未驶出）记录
     * 
     * @param plateNumber 车牌号
     * @return 活跃记录
     */
    PmsVehicleRecord selectActiveRecordByPlate(String plateNumber);

    /**
     * 处理车辆进场
     * 
     * @param file 进场抓拍图片
     * @param communityId 小区ID
     * @param parkingId 停车场ID
     * @param plateNumber 车牌号
     * @param vehicleType 车辆类型
     * @return 生成的车辆记录
     */
    PmsVehicleRecord handleVehicleEnter(MultipartFile file, Long communityId, Long parkingId, String plateNumber, String vehicleType);

    /**
     * 处理车辆出场
     * 
     * @param file 出场抓拍图片
     * @param plateNumber 车牌号
     * @return 更新后的车辆记录
     */
    PmsVehicleRecord handleVehicleExit(MultipartFile file, String plateNumber);
}
