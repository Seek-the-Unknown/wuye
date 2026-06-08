package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsBuilding;

/**
 * 楼栋管理Service接口
 * 
 * @author ruoyi
 */
public interface IPmsBuildingService 
{
    /**
     * 查询楼栋管理
     * 
     * @param buildingId 楼栋管理主键
     * @return 楼栋管理
     */
    public PmsBuilding selectPmsBuildingByBuildingId(Long buildingId);

    /**
     * 查询楼栋管理列表
     * 
     * @param pmsBuilding 楼栋管理
     * @return 楼栋管理集合
     */
    public List<PmsBuilding> selectPmsBuildingList(PmsBuilding pmsBuilding);

    /**
     * 新增楼栋管理
     * 
     * @param pmsBuilding 楼栋管理
     * @return 结果
     */
    public int insertPmsBuilding(PmsBuilding pmsBuilding);

    /**
     * 修改楼栋管理
     * 
     * @param pmsBuilding 楼栋管理
     * @return 结果
     */
    public int updatePmsBuilding(PmsBuilding pmsBuilding);

    /**
     * 批量删除楼栋管理
     * 
     * @param buildingIds 需要删除的楼栋管理主键集合
     * @return 结果
     */
    public int deletePmsBuildingByBuildingIds(Long[] buildingIds);

    /**
     * 删除楼栋管理信息
     * 
     * @param buildingId 楼栋管理主键
     * @return 结果
     */
    public int deletePmsBuildingByBuildingId(Long buildingId);
}
