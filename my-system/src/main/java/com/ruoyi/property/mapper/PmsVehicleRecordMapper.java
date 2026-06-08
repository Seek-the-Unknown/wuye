package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsVehicleRecord;

public interface PmsVehicleRecordMapper {
    PmsVehicleRecord selectPmsVehicleRecordByRecordId(Long recordId);
    List<PmsVehicleRecord> selectPmsVehicleRecordList(PmsVehicleRecord record);
    int insertPmsVehicleRecord(PmsVehicleRecord record);
    int updatePmsVehicleRecord(PmsVehicleRecord record);
    int deletePmsVehicleRecordByRecordId(Long recordId);
    int deletePmsVehicleRecordByRecordIds(Long[] recordIds);
    PmsVehicleRecord selectActiveRecordByPlate(String plateNumber);
}
