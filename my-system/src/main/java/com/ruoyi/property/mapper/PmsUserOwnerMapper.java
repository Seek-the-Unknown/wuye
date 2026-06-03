package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsUserOwner;

public interface PmsUserOwnerMapper {
    /** 根据userId查询绑定关系 */
    PmsUserOwner selectByUserId(Long userId);
    /** 根据主键ID查询绑定关系 */
    PmsUserOwner selectPmsUserOwnerById(Long id);
    /** 查询列表 */
    List<PmsUserOwner> selectPmsUserOwnerList(PmsUserOwner query);
    /** 插入 */
    int insertPmsUserOwner(PmsUserOwner pmsUserOwner);
    /** 更新 */
    int updatePmsUserOwner(PmsUserOwner pmsUserOwner);
    /** 删除 */
    int deletePmsUserOwnerById(Long id);
}
