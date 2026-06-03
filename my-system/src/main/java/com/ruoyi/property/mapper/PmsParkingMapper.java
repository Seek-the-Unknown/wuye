package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsParking;

public interface PmsParkingMapper {
    PmsParking selectPmsParkingByParkingId(Long parkingId);
    List<PmsParking> selectPmsParkingList(PmsParking pmsParking);
    int insertPmsParking(PmsParking pmsParking);
    int updatePmsParking(PmsParking pmsParking);
    int deletePmsParkingByParkingId(Long parkingId);
    int deletePmsParkingByParkingIds(Long[] parkingIds);
}
