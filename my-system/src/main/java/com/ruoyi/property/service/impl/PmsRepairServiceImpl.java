package com.ruoyi.property.service.impl;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsRepairMapper;
import com.ruoyi.property.domain.PmsRepair;
import com.ruoyi.property.service.IPmsRepairService;

@Service
public class PmsRepairServiceImpl implements IPmsRepairService 
{
    @Autowired
    private PmsRepairMapper pmsRepairMapper;

    @Override
    public PmsRepair selectPmsRepairByRepairId(Long repairId) { return pmsRepairMapper.selectPmsRepairByRepairId(repairId); }
    @Override
    public List<PmsRepair> selectPmsRepairList(PmsRepair pmsRepair) { return pmsRepairMapper.selectPmsRepairList(pmsRepair); }
    @Override
    public int insertPmsRepair(PmsRepair pmsRepair) { pmsRepair.setCreateTime(DateUtils.getNowDate()); return pmsRepairMapper.insertPmsRepair(pmsRepair); }
    @Override
    public int updatePmsRepair(PmsRepair pmsRepair) { pmsRepair.setUpdateTime(DateUtils.getNowDate()); return pmsRepairMapper.updatePmsRepair(pmsRepair); }
    @Override
    public int deletePmsRepairByRepairIds(Long[] repairIds) { return pmsRepairMapper.deletePmsRepairByRepairIds(repairIds); }
    @Override
    public int deletePmsRepairByRepairId(Long repairId) { return pmsRepairMapper.deletePmsRepairByRepairId(repairId); }
}
