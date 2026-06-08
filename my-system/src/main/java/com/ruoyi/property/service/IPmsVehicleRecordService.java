package com.ruoyi.property.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.property.domain.PmsVehicleRecord;

public interface IPmsVehicleRecordService {
    PmsVehicleRecord selectPmsVehicleRecordByRecordId(Long recordId);
    List<PmsVehicleRecord> selectPmsVehicleRecordList(PmsVehicleRecord record);
    int insertPmsVehicleRecord(PmsVehicleRecord record);
    int updatePmsVehicleRecord(PmsVehicleRecord record);
    int deletePmsVehicleRecordByRecordIds(Long[] recordIds);
    int deletePmsVehicleRecordByRecordId(Long recordId);
    PmsVehicleRecord selectActiveRecordByPlate(String plateNumber);
    PmsVehicleRecord handleVehicleEnter(MultipartFile file, Long communityId, Long parkingId, String plateNumber, String vehicleType);
    PmsVehicleRecord handleVehicleExit(MultipartFile file, String plateNumber);
}
