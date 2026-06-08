package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsOwnerMapper;
import com.ruoyi.property.domain.PmsOwner;
import com.ruoyi.property.service.IPmsOwnerService;

/**
 * 业主管理Service业务层实现类
 * 负责处理业主信息管理的业务逻辑
 * 
 * @author ruoyi
 */
@Service
public class PmsOwnerServiceImpl implements IPmsOwnerService 
{
    /** 业主管理Mapper数据访问接口 */
    @Autowired
    private PmsOwnerMapper pmsOwnerMapper;

    /**
     * 根据业主ID查询业主详细信息
     * 
     * @param ownerId 业主管理主键ID
     * @return 业主管理实体对象
     */
    @Override
    public PmsOwner selectPmsOwnerByOwnerId(Long ownerId)
    {
        // 调用Mapper接口根据主键查询单条业主记录
        return pmsOwnerMapper.selectPmsOwnerByOwnerId(ownerId);
    }

    /**
     * 查询符合条件的业主管理列表数据
     * 
     * @param pmsOwner 包含查询条件的业主管理实体
     * @return 业主管理对象集合
     */
    @Override
    public List<PmsOwner> selectPmsOwnerList(PmsOwner pmsOwner)
    {
        // 调用Mapper接口根据条件查询业主列表
        return pmsOwnerMapper.selectPmsOwnerList(pmsOwner);
    }

    /**
     * 新增业主管理记录
     * 
     * @param pmsOwner 待新增的业主管理实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsOwner(PmsOwner pmsOwner)
    {
        // 自动设置记录的创建时间为当前时间
        pmsOwner.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入业主操作
        return pmsOwnerMapper.insertPmsOwner(pmsOwner);
    }

    /**
     * 修改业主管理记录
     * 
     * @param pmsOwner 包含修改信息的业主管理实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsOwner(PmsOwner pmsOwner)
    {
        // 自动设置记录的更新时间为当前时间
        pmsOwner.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新业主操作
        return pmsOwnerMapper.updatePmsOwner(pmsOwner);
    }

    /**
     * 批量删除业主管理记录
     * 
     * @param ownerIds 需要批量删除的业主管理主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsOwnerByOwnerIds(Long[] ownerIds)
    {
        // 调用Mapper接口根据主键数组批量删除业主记录
        return pmsOwnerMapper.deletePmsOwnerByOwnerIds(ownerIds);
    }

    /**
     * 根据业主ID单条删除业主管理信息
     * 
     * @param ownerId 待删除的业主管理主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsOwnerByOwnerId(Long ownerId)
    {
        // 调用Mapper接口根据主键删除单条业主记录
        return pmsOwnerMapper.deletePmsOwnerByOwnerId(ownerId);
    }
}
