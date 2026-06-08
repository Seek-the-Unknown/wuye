package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsParking;

/**
 * 车位管理Service接口
 * 
 * @author ruoyi
 */
public interface IPmsParkingService {
    /**
     * 查询车位管理
     * 
     * @param parkingId 车位管理主键
     * @return 车位管理
     */
    PmsParking selectPmsParkingByParkingId(Long parkingId);

    /**
     * 查询车位管理列表
     * 
     * @param pmsParking 车位管理
     * @return 车位管理集合
     */
    List<PmsParking> selectPmsParkingList(PmsParking pmsParking);

    /**
     * 新增车位管理
     * 
     * @param pmsParking 车位管理
     * @return 结果
     */
    int insertPmsParking(PmsParking pmsParking);

    /**
     * 修改车位管理
     * 
     * @param pmsParking 车位管理
     * @return 结果
     */
    int updatePmsParking(PmsParking pmsParking);

    /**
     * 批量删除车位管理
     * 
     * @param parkingIds 需要删除的车位管理主键集合
     * @return 结果
     */
    int deletePmsParkingByParkingIds(Long[] parkingIds);

    /**
     * 删除车位管理信息
     * 
     * @param parkingId 车位管理主键
     * @return 结果
     */
    int deletePmsParkingByParkingId(Long parkingId);
}
