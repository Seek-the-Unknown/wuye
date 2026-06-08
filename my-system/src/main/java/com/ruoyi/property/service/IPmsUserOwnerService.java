package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsUserOwner;

/**
 * 用户业主关联Service接口
 * 
 * @author ruoyi
 */
public interface IPmsUserOwnerService {
    /**
     * 根据用户ID查询关联的业主信息
     * 
     * @param userId 用户主键
     * @return 关联的业主信息
     */
    PmsUserOwner selectByUserId(Long userId);

    /**
     * 查询用户业主关联记录
     * 
     * @param id 用户业主关联主键
     * @return 用户业主关联信息
     */
    PmsUserOwner selectPmsUserOwnerById(Long id);

    /**
     * 查询用户业主关联列表
     * 
     * @param query 查询条件
     * @return 关联信息集合
     */
    List<PmsUserOwner> selectPmsUserOwnerList(PmsUserOwner query);

    /**
     * 新增用户业主关联
     * 
     * @param pmsUserOwner 关联信息
     * @return 结果
     */
    int insertPmsUserOwner(PmsUserOwner pmsUserOwner);

    /**
     * 修改用户业主关联
     * 
     * @param pmsUserOwner 关联信息
     * @return 结果
     */
    int updatePmsUserOwner(PmsUserOwner pmsUserOwner);

    /**
     * 删除用户业主关联
     * 
     * @param id 关联主键
     * @return 结果
     */
    int deletePmsUserOwnerById(Long id);
}
