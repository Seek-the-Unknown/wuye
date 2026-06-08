package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsRepair;

/**
 * 报修管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsRepairMapper 
{
    /**
     * 查询报修管理
     * 
     * @param repairId 报修管理主键
     * @return 报修管理
     */
    public PmsRepair selectPmsRepairByRepairId(Long repairId);

    /**
     * 查询报修管理列表
     * 
     * @param pmsRepair 报修管理
     * @return 报修管理集合
     */
    public List<PmsRepair> selectPmsRepairList(PmsRepair pmsRepair);

    /**
     * 新增报修管理
     * 
     * @param pmsRepair 报修管理
     * @return 结果
     */
    public int insertPmsRepair(PmsRepair pmsRepair);

    /**
     * 修改报修管理
     * 
     * @param pmsRepair 报修管理
     * @return 结果
     */
    public int updatePmsRepair(PmsRepair pmsRepair);

    /**
     * 删除报修管理
     * 
     * @param repairId 报修管理主键
     * @return 结果
     */
    public int deletePmsRepairByRepairId(Long repairId);

    /**
     * 批量删除报修管理
     * 
     * @param repairIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsRepairByRepairIds(Long[] repairIds);
}
