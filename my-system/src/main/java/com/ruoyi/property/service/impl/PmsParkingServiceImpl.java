package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsParkingMapper;
import com.ruoyi.property.domain.PmsParking;
import com.ruoyi.property.service.IPmsParkingService;

/**
 * 车位管理Service业务层实现类
 * 负责处理车位资源分配及管理的业务逻辑
 * 
 * @author ruoyi
 */
@Service
public class PmsParkingServiceImpl implements IPmsParkingService {
    
    /** 车位管理Mapper数据访问接口 */
    @Autowired
    private PmsParkingMapper pmsParkingMapper;

    /**
     * 根据车位ID查询车位管理详细信息
     * 
     * @param parkingId 车位管理主键ID
     * @return 车位管理实体对象
     */
    @Override
    public PmsParking selectPmsParkingByParkingId(Long parkingId) {
        // 调用Mapper接口根据主键查询车位详细信息
        return pmsParkingMapper.selectPmsParkingByParkingId(parkingId);
    }

    /**
     * 查询符合条件的车位管理列表数据
     * 
     * @param pmsParking 包含查询条件的车位管理实体
     * @return 车位管理对象集合
     */
    @Override
    public List<PmsParking> selectPmsParkingList(PmsParking pmsParking) {
        // 调用Mapper接口根据条件查询车位列表
        return pmsParkingMapper.selectPmsParkingList(pmsParking);
    }

    /**
     * 新增车位管理记录
     * 
     * @param pmsParking 待新增的车位管理实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsParking(PmsParking pmsParking) {
        // 自动设置记录的创建时间为当前时间
        pmsParking.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入车位数据操作
        return pmsParkingMapper.insertPmsParking(pmsParking);
    }

    /**
     * 修改车位管理记录
     * 
     * @param pmsParking 包含修改信息的车位管理实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsParking(PmsParking pmsParking) {
        // 自动设置记录的更新时间为当前时间
        pmsParking.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新车位数据操作
        return pmsParkingMapper.updatePmsParking(pmsParking);
    }

    /**
     * 批量删除车位管理记录
     * 
     * @param parkingIds 需要批量删除的车位管理主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsParkingByParkingIds(Long[] parkingIds) {
        // 调用Mapper接口根据主键数组批量删除车位记录
        return pmsParkingMapper.deletePmsParkingByParkingIds(parkingIds);
    }

    /**
     * 根据车位ID单条删除车位管理信息
     * 
     * @param parkingId 待删除的车位管理主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsParkingByParkingId(Long parkingId) {
        // 调用Mapper接口根据主键删除单条车位记录
        return pmsParkingMapper.deletePmsParkingByParkingId(parkingId);
    }
}
