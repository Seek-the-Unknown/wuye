package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsParkingMapper;
import com.ruoyi.property.domain.PmsParking;
import com.ruoyi.property.service.IPmsParkingService;

@Service
public class PmsParkingServiceImpl implements IPmsParkingService {
    @Autowired
    private PmsParkingMapper pmsParkingMapper;

    @Override
    public PmsParking selectPmsParkingByParkingId(Long parkingId) {
        return pmsParkingMapper.selectPmsParkingByParkingId(parkingId);
    }

    @Override
    public List<PmsParking> selectPmsParkingList(PmsParking pmsParking) {
        return pmsParkingMapper.selectPmsParkingList(pmsParking);
    }

    @Override
    public int insertPmsParking(PmsParking pmsParking) {
        pmsParking.setCreateTime(DateUtils.getNowDate());
        return pmsParkingMapper.insertPmsParking(pmsParking);
    }

    @Override
    public int updatePmsParking(PmsParking pmsParking) {
        pmsParking.setUpdateTime(DateUtils.getNowDate());
        return pmsParkingMapper.updatePmsParking(pmsParking);
    }

    @Override
    public int deletePmsParkingByParkingIds(Long[] parkingIds) {
        return pmsParkingMapper.deletePmsParkingByParkingIds(parkingIds);
    }

    @Override
    public int deletePmsParkingByParkingId(Long parkingId) {
        return pmsParkingMapper.deletePmsParkingByParkingId(parkingId);
    }
}
