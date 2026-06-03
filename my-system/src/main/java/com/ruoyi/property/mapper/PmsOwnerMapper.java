package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsOwner;

/**
 * 业主管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsOwnerMapper 
{
    /**
     * 查询业主管理
     * 
     * @param ownerId 业主管理主键
     * @return 业主管理
     */
    public PmsOwner selectPmsOwnerByOwnerId(Long ownerId);

    /**
     * 查询业主管理列表
     * 
     * @param pmsOwner 业主管理
     * @return 业主管理集合
     */
    public List<PmsOwner> selectPmsOwnerList(PmsOwner pmsOwner);

    /**
     * 新增业主管理
     * 
     * @param pmsOwner 业主管理
     * @return 结果
     */
    public int insertPmsOwner(PmsOwner pmsOwner);

    /**
     * 修改业主管理
     * 
     * @param pmsOwner 业主管理
     * @return 结果
     */
    public int updatePmsOwner(PmsOwner pmsOwner);

    /**
     * 删除业主管理
     * 
     * @param ownerId 业主管理主键
     * @return 结果
     */
    public int deletePmsOwnerByOwnerId(Long ownerId);

    /**
     * 批量删除业主管理
     * 
     * @param ownerIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsOwnerByOwnerIds(Long[] ownerIds);
}
