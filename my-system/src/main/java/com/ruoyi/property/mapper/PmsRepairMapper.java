package com.ruoyi.property.mapper;
import java.util.List;
import com.ruoyi.property.domain.PmsRepair;

public interface PmsRepairMapper 
{
    public PmsRepair selectPmsRepairByRepairId(Long repairId);
    public List<PmsRepair> selectPmsRepairList(PmsRepair pmsRepair);
    public int insertPmsRepair(PmsRepair pmsRepair);
    public int updatePmsRepair(PmsRepair pmsRepair);
    public int deletePmsRepairByRepairId(Long repairId);
    public int deletePmsRepairByRepairIds(Long[] repairIds);
}
