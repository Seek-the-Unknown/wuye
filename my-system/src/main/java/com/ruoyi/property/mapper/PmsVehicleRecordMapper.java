package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsVehicleRecord;

/**
 * 车辆进出记录管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsVehicleRecordMapper {
    /**
     * 查询车辆进出记录管理
     * 
     * @param recordId 车辆进出记录管理主键
     * @return 车辆进出记录管理
     */
    public PmsVehicleRecord selectPmsVehicleRecordByRecordId(Long recordId);

    /**
     * 查询车辆进出记录管理列表
     * 
     * @param record 车辆进出记录管理
     * @return 车辆进出记录管理集合
     */
    public List<PmsVehicleRecord> selectPmsVehicleRecordList(PmsVehicleRecord record);

    /**
     * 新增车辆进出记录管理
     * 
     * @param record 车辆进出记录管理
     * @return 结果
     */
    public int insertPmsVehicleRecord(PmsVehicleRecord record);

    /**
     * 修改车辆进出记录管理
     * 
     * @param record 车辆进出记录管理
     * @return 结果
     */
    public int updatePmsVehicleRecord(PmsVehicleRecord record);

    /**
     * 删除车辆进出记录管理
     * 
     * @param recordId 车辆进出记录管理主键
     * @return 结果
     */
    public int deletePmsVehicleRecordByRecordId(Long recordId);

    /**
     * 批量删除车辆进出记录管理
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsVehicleRecordByRecordIds(Long[] recordIds);

    /**
     * 根据车牌号查询活跃的进出记录
     * 
     * @param plateNumber 车牌号
     * @return 车辆进出记录
     */
    public PmsVehicleRecord selectActiveRecordByPlate(String plateNumber);
}
