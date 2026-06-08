package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsCommunityMapper;
import com.ruoyi.property.domain.PmsCommunity;
import com.ruoyi.property.service.IPmsCommunityService;

/**
 * 小区管理Service业务层实现类
 * 负责处理小区相关的业务逻辑操作
 * 
 * @author ruoyi
 */
@Service
public class PmsCommunityServiceImpl implements IPmsCommunityService 
{
    /** 小区管理Mapper数据访问接口 */
    @Autowired
    private PmsCommunityMapper pmsCommunityMapper;

    /**
     * 根据小区ID查询小区详细信息
     * 
     * @param communityId 小区管理主键ID
     * @return 小区管理实体对象
     */
    @Override
    public PmsCommunity selectPmsCommunityByCommunityId(Long communityId)
    {
        // 调用Mapper接口根据主键查询小区信息
        return pmsCommunityMapper.selectPmsCommunityByCommunityId(communityId);
    }

    /**
     * 查询符合条件的小区管理列表数据
     * 
     * @param pmsCommunity 包含查询条件的小区管理实体
     * @return 小区管理对象集合
     */
    @Override
    public List<PmsCommunity> selectPmsCommunityList(PmsCommunity pmsCommunity)
    {
        // 调用Mapper接口根据条件查询小区列表
        return pmsCommunityMapper.selectPmsCommunityList(pmsCommunity);
    }

    /**
     * 新增小区管理记录
     * 
     * @param pmsCommunity 待新增的小区管理实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsCommunity(PmsCommunity pmsCommunity)
    {
        // 自动设置记录的创建时间为当前系统时间
        pmsCommunity.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入小区数据操作
        return pmsCommunityMapper.insertPmsCommunity(pmsCommunity);
    }

    /**
     * 修改小区管理记录
     * 
     * @param pmsCommunity 包含修改信息的小区管理实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsCommunity(PmsCommunity pmsCommunity)
    {
        // 自动设置记录的更新时间为当前系统时间
        pmsCommunity.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新小区数据操作
        return pmsCommunityMapper.updatePmsCommunity(pmsCommunity);
    }

    /**
     * 批量删除小区管理记录
     * 
     * @param communityIds 需要批量删除的小区管理主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsCommunityByCommunityIds(Long[] communityIds)
    {
        // 调用Mapper接口根据主键数组批量删除小区记录
        return pmsCommunityMapper.deletePmsCommunityByCommunityIds(communityIds);
    }

    /**
     * 根据小区ID单条删除小区管理信息
     * 
     * @param communityId 待删除的小区管理主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsCommunityByCommunityId(Long communityId)
    {
        // 调用Mapper接口根据主键删除单条小区记录
        return pmsCommunityMapper.deletePmsCommunityByCommunityId(communityId);
    }
}
