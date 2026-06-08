package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsRepairMapper;
import com.ruoyi.property.domain.PmsRepair;
import com.ruoyi.property.service.IPmsRepairService;

/**
 * 报修管理Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class PmsRepairServiceImpl implements IPmsRepairService 
{
    @Autowired
    private PmsRepairMapper pmsRepairMapper;

    /**
     * 查询报修管理
     * 
     * @param repairId 报修管理主键
     * @return 报修管理
     */
    @Override
    public PmsRepair selectPmsRepairByRepairId(Long repairId) {
        return pmsRepairMapper.selectPmsRepairByRepairId(repairId);
    }

    /**
     * 查询报修管理列表
     * 
     * @param pmsRepair 报修管理
     * @return 报修管理集合
     */
    @Override
    public List<PmsRepair> selectPmsRepairList(PmsRepair pmsRepair) {
        return pmsRepairMapper.selectPmsRepairList(pmsRepair);
    }

    /**
     * 新增报修管理
     * 
     * @param pmsRepair 报修管理
     * @return 结果
     */
    @Override
    public int insertPmsRepair(PmsRepair pmsRepair) {
        // 设置创建时间
        pmsRepair.setCreateTime(DateUtils.getNowDate());
        return pmsRepairMapper.insertPmsRepair(pmsRepair);
    }

    /**
     * 修改报修管理
     * 
     * @param pmsRepair 报修管理
     * @return 结果
     */
    @Override
    public int updatePmsRepair(PmsRepair pmsRepair) {
        // 设置更新时间
        pmsRepair.setUpdateTime(DateUtils.getNowDate());
        return pmsRepairMapper.updatePmsRepair(pmsRepair);
    }

    /**
     * 批量删除报修管理
     * 
     * @param repairIds 需要删除的报修管理主键集合
     * @return 结果
     */
    @Override
    public int deletePmsRepairByRepairIds(Long[] repairIds) {
        return pmsRepairMapper.deletePmsRepairByRepairIds(repairIds);
    }

    /**
     * 删除报修管理信息
     * 
     * @param repairId 报修管理主键
     * @return 结果
     */
    @Override
    public int deletePmsRepairByRepairId(Long repairId) {
        return pmsRepairMapper.deletePmsRepairByRepairId(repairId);
    }
}
