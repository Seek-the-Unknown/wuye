package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsCommunity;

/**
 * 小区管理Service接口
 * 
 * @author ruoyi
 */
public interface IPmsCommunityService 
{
    /**
     * 查询小区管理
     * 
     * @param communityId 小区管理主键
     * @return 小区管理
     */
    public PmsCommunity selectPmsCommunityByCommunityId(Long communityId);

    /**
     * 查询小区管理列表
     * 
     * @param pmsCommunity 小区管理
     * @return 小区管理集合
     */
    public List<PmsCommunity> selectPmsCommunityList(PmsCommunity pmsCommunity);

    /**
     * 新增小区管理
     * 
     * @param pmsCommunity 小区管理
     * @return 结果
     */
    public int insertPmsCommunity(PmsCommunity pmsCommunity);

    /**
     * 修改小区管理
     * 
     * @param pmsCommunity 小区管理
     * @return 结果
     */
    public int updatePmsCommunity(PmsCommunity pmsCommunity);

    /**
     * 批量删除小区管理
     * 
     * @param communityIds 需要删除的小区管理主键集合
     * @return 结果
     */
    public int deletePmsCommunityByCommunityIds(Long[] communityIds);

    /**
     * 删除小区管理信息
     * 
     * @param communityId 小区管理主键
     * @return 结果
     */
    public int deletePmsCommunityByCommunityId(Long communityId);
}
