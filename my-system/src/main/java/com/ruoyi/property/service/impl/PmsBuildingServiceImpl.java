package com.ruoyi.property.service.impl;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsBuildingMapper;
import com.ruoyi.property.domain.PmsBuilding;
import com.ruoyi.property.service.IPmsBuildingService;

@Service
public class PmsBuildingServiceImpl implements IPmsBuildingService 
{
    @Autowired
    private PmsBuildingMapper pmsBuildingMapper;

    @Override
    public PmsBuilding selectPmsBuildingByBuildingId(Long buildingId) {
        return pmsBuildingMapper.selectPmsBuildingByBuildingId(buildingId);
    }
    @Override
    public List<PmsBuilding> selectPmsBuildingList(PmsBuilding pmsBuilding) {
        return pmsBuildingMapper.selectPmsBuildingList(pmsBuilding);
    }
    @Override
    public int insertPmsBuilding(PmsBuilding pmsBuilding) {
        pmsBuilding.setCreateTime(DateUtils.getNowDate());
        return pmsBuildingMapper.insertPmsBuilding(pmsBuilding);
    }
    @Override
    public int updatePmsBuilding(PmsBuilding pmsBuilding) {
        pmsBuilding.setUpdateTime(DateUtils.getNowDate());
        return pmsBuildingMapper.updatePmsBuilding(pmsBuilding);
    }
    @Override
    public int deletePmsBuildingByBuildingIds(Long[] buildingIds) {
        return pmsBuildingMapper.deletePmsBuildingByBuildingIds(buildingIds);
    }
    @Override
    public int deletePmsBuildingByBuildingId(Long buildingId) {
        return pmsBuildingMapper.deletePmsBuildingByBuildingId(buildingId);
    }
}
