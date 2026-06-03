package com.ruoyi.property.mapper;
import java.util.List;
import com.ruoyi.property.domain.PmsBuilding;

public interface PmsBuildingMapper 
{
    public PmsBuilding selectPmsBuildingByBuildingId(Long buildingId);
    public List<PmsBuilding> selectPmsBuildingList(PmsBuilding pmsBuilding);
    public int insertPmsBuilding(PmsBuilding pmsBuilding);
    public int updatePmsBuilding(PmsBuilding pmsBuilding);
    public int deletePmsBuildingByBuildingId(Long buildingId);
    public int deletePmsBuildingByBuildingIds(Long[] buildingIds);
}
