package com.ruoyi.property.service;
import java.util.List;
import com.ruoyi.property.domain.PmsBuilding;

public interface IPmsBuildingService 
{
    public PmsBuilding selectPmsBuildingByBuildingId(Long buildingId);
    public List<PmsBuilding> selectPmsBuildingList(PmsBuilding pmsBuilding);
    public int insertPmsBuilding(PmsBuilding pmsBuilding);
    public int updatePmsBuilding(PmsBuilding pmsBuilding);
    public int deletePmsBuildingByBuildingIds(Long[] buildingIds);
    public int deletePmsBuildingByBuildingId(Long buildingId);
}
