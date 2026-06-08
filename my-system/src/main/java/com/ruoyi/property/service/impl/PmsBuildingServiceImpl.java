package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsBuildingMapper;
import com.ruoyi.property.domain.PmsBuilding;
import com.ruoyi.property.service.IPmsBuildingService;

/**
 * 楼栋管理Service业务层实现类
 * 负责处理与楼栋管理相关的核心业务逻辑
 * 
 * @author ruoyi
 */
@Service
public class PmsBuildingServiceImpl implements IPmsBuildingService 
{
    /** 楼栋管理Mapper数据访问接口 */
    @Autowired
    private PmsBuildingMapper pmsBuildingMapper;

    /**
     * 根据楼栋ID查询楼栋管理详细信息
     * 
     * @param buildingId 楼栋管理主键ID
     * @return 楼栋管理实体对象
     */
    @Override
    public PmsBuilding selectPmsBuildingByBuildingId(Long buildingId) {
        // 调用Mapper接口根据主键查询楼栋信息
        return pmsBuildingMapper.selectPmsBuildingByBuildingId(buildingId);
    }

    /**
     * 查询楼栋管理列表数据
     * 
     * @param pmsBuilding 包含查询条件的楼栋管理实体
     * @return 楼栋管理对象集合
     */
    @Override
    public List<PmsBuilding> selectPmsBuildingList(PmsBuilding pmsBuilding) {
        // 调用Mapper接口根据条件查询楼栋列表
        return pmsBuildingMapper.selectPmsBuildingList(pmsBuilding);
    }

    /**
     * 新增楼栋管理记录
     * 
     * @param pmsBuilding 待新增的楼栋管理实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsBuilding(PmsBuilding pmsBuilding) {
        // 自动设置记录的创建时间为当前时间
        pmsBuilding.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入操作
        return pmsBuildingMapper.insertPmsBuilding(pmsBuilding);
    }

    /**
     * 修改楼栋管理记录
     * 
     * @param pmsBuilding 包含修改信息的楼栋管理实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsBuilding(PmsBuilding pmsBuilding) {
        // 自动设置记录的更新时间为当前时间
        pmsBuilding.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新操作
        return pmsBuildingMapper.updatePmsBuilding(pmsBuilding);
    }

    /**
     * 批量删除楼栋管理记录
     * 
     * @param buildingIds 需要批量删除的楼栋管理主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsBuildingByBuildingIds(Long[] buildingIds) {
        // 调用Mapper接口根据主键数组批量删除楼栋记录
        return pmsBuildingMapper.deletePmsBuildingByBuildingIds(buildingIds);
    }

    /**
     * 根据楼栋ID单条删除楼栋管理信息
     * 
     * @param buildingId 待删除的楼栋管理主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsBuildingByBuildingId(Long buildingId) {
        // 调用Mapper接口根据主键删除单条楼栋记录
        return pmsBuildingMapper.deletePmsBuildingByBuildingId(buildingId);
    }
}
