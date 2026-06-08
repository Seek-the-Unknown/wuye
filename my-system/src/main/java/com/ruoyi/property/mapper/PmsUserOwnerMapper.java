package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsUserOwner;

/**
 * 业主用户绑定关联Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsUserOwnerMapper {
    /**
     * 根据userId查询绑定关系
     * 
     * @param userId 用户主键
     * @return 用户业主关联
     */
    PmsUserOwner selectByUserId(Long userId);

    /**
     * 根据主键ID查询绑定关系
     * 
     * @param id 主键
     * @return 用户业主关联
     */
    PmsUserOwner selectPmsUserOwnerById(Long id);

    /**
     * 查询列表
     * 
     * @param query 查询条件
     * @return 关联列表
     */
    List<PmsUserOwner> selectPmsUserOwnerList(PmsUserOwner query);

    /**
     * 插入用户业主绑定
     * 
     * @param pmsUserOwner 用户业主绑定
     * @return 结果
     */
    int insertPmsUserOwner(PmsUserOwner pmsUserOwner);

    /**
     * 更新用户业主绑定
     * 
     * @param pmsUserOwner 用户业主绑定
     * @return 结果
     */
    int updatePmsUserOwner(PmsUserOwner pmsUserOwner);

    /**
     * 删除绑定关系
     * 
     * @param id 主键
     * @return 结果
     */
    int deletePmsUserOwnerById(Long id);
}
