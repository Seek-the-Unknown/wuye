package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsOwnerMapper;
import com.ruoyi.property.domain.PmsOwner;
import com.ruoyi.property.service.IPmsOwnerService;

/**
 * 业主管理Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class PmsOwnerServiceImpl implements IPmsOwnerService 
{
    @Autowired
    private PmsOwnerMapper pmsOwnerMapper;

    /**
     * 查询业主管理
     * 
     * @param ownerId 业主管理主键
     * @return 业主管理
     */
    @Override
    public PmsOwner selectPmsOwnerByOwnerId(Long ownerId)
    {
        return pmsOwnerMapper.selectPmsOwnerByOwnerId(ownerId);
    }

    /**
     * 查询业主管理列表
     * 
     * @param pmsOwner 业主管理
     * @return 业主管理
     */
    @Override
    public List<PmsOwner> selectPmsOwnerList(PmsOwner pmsOwner)
    {
        return pmsOwnerMapper.selectPmsOwnerList(pmsOwner);
    }

    /**
     * 新增业主管理
     * 
     * @param pmsOwner 业主管理
     * @return 结果
     */
    @Override
    public int insertPmsOwner(PmsOwner pmsOwner)
    {
        pmsOwner.setCreateTime(DateUtils.getNowDate());
        return pmsOwnerMapper.insertPmsOwner(pmsOwner);
    }

    /**
     * 修改业主管理
     * 
     * @param pmsOwner 业主管理
     * @return 结果
     */
    @Override
    public int updatePmsOwner(PmsOwner pmsOwner)
    {
        pmsOwner.setUpdateTime(DateUtils.getNowDate());
        return pmsOwnerMapper.updatePmsOwner(pmsOwner);
    }

    /**
     * 批量删除业主管理
     * 
     * @param ownerIds 需要删除的业主管理主键
     * @return 结果
     */
    @Override
    public int deletePmsOwnerByOwnerIds(Long[] ownerIds)
    {
        return pmsOwnerMapper.deletePmsOwnerByOwnerIds(ownerIds);
    }

    /**
     * 删除业主管理信息
     * 
     * @param ownerId 业主管理主键
     * @return 结果
     */
    @Override
    public int deletePmsOwnerByOwnerId(Long ownerId)
    {
        return pmsOwnerMapper.deletePmsOwnerByOwnerId(ownerId);
    }
}
