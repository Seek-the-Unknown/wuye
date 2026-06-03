package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsCommunityMapper;
import com.ruoyi.property.domain.PmsCommunity;
import com.ruoyi.property.service.IPmsCommunityService;

/**
 * 小区管理Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class PmsCommunityServiceImpl implements IPmsCommunityService 
{
    @Autowired
    private PmsCommunityMapper pmsCommunityMapper;

    /**
     * 查询小区管理
     * 
     * @param communityId 小区管理主键
     * @return 小区管理
     */
    @Override
    public PmsCommunity selectPmsCommunityByCommunityId(Long communityId)
    {
        return pmsCommunityMapper.selectPmsCommunityByCommunityId(communityId);
    }

    /**
     * 查询小区管理列表
     * 
     * @param pmsCommunity 小区管理
     * @return 小区管理
     */
    @Override
    public List<PmsCommunity> selectPmsCommunityList(PmsCommunity pmsCommunity)
    {
        return pmsCommunityMapper.selectPmsCommunityList(pmsCommunity);
    }

    /**
     * 新增小区管理
     * 
     * @param pmsCommunity 小区管理
     * @return 结果
     */
    @Override
    public int insertPmsCommunity(PmsCommunity pmsCommunity)
    {
        pmsCommunity.setCreateTime(DateUtils.getNowDate());
        return pmsCommunityMapper.insertPmsCommunity(pmsCommunity);
    }

    /**
     * 修改小区管理
     * 
     * @param pmsCommunity 小区管理
     * @return 结果
     */
    @Override
    public int updatePmsCommunity(PmsCommunity pmsCommunity)
    {
        pmsCommunity.setUpdateTime(DateUtils.getNowDate());
        return pmsCommunityMapper.updatePmsCommunity(pmsCommunity);
    }

    /**
     * 批量删除小区管理
     * 
     * @param communityIds 需要删除的小区管理主键
     * @return 结果
     */
    @Override
    public int deletePmsCommunityByCommunityIds(Long[] communityIds)
    {
        return pmsCommunityMapper.deletePmsCommunityByCommunityIds(communityIds);
    }

    /**
     * 删除小区管理信息
     * 
     * @param communityId 小区管理主键
     * @return 结果
     */
    @Override
    public int deletePmsCommunityByCommunityId(Long communityId)
    {
        return pmsCommunityMapper.deletePmsCommunityByCommunityId(communityId);
    }
}
