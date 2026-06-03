package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsParking;

public interface IPmsParkingService {
    PmsParking selectPmsParkingByParkingId(Long parkingId);
    List<PmsParking> selectPmsParkingList(PmsParking pmsParking);
    int insertPmsParking(PmsParking pmsParking);
    int updatePmsParking(PmsParking pmsParking);
    int deletePmsParkingByParkingIds(Long[] parkingIds);
    int deletePmsParkingByParkingId(Long parkingId);
}
