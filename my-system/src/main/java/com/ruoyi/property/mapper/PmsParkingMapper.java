package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsParking;

/**
 * 车位管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsParkingMapper {
    /**
     * 查询车位管理
     * 
     * @param parkingId 车位管理主键
     * @return 车位管理
     */
    public PmsParking selectPmsParkingByParkingId(Long parkingId);

    /**
     * 查询车位管理列表
     * 
     * @param pmsParking 车位管理
     * @return 车位管理集合
     */
    public List<PmsParking> selectPmsParkingList(PmsParking pmsParking);

    /**
     * 根据车牌号查询车位信息
     * 
     * @param plateNumber 车牌号
     * @return 车位管理
     */
    public PmsParking selectPmsParkingByPlateNumber(String plateNumber);

    /**
     * 新增车位管理
     * 
     * @param pmsParking 车位管理
     * @return 结果
     */
    public int insertPmsParking(PmsParking pmsParking);

    /**
     * 修改车位管理
     * 
     * @param pmsParking 车位管理
     * @return 结果
     */
    public int updatePmsParking(PmsParking pmsParking);

    /**
     * 删除车位管理
     * 
     * @param parkingId 车位管理主键
     * @return 结果
     */
    public int deletePmsParkingByParkingId(Long parkingId);

    /**
     * 批量删除车位管理
     * 
     * @param parkingIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsParkingByParkingIds(Long[] parkingIds);
}
