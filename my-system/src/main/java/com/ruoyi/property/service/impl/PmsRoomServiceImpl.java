package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsRoomMapper;
import com.ruoyi.property.domain.PmsRoom;
import com.ruoyi.property.service.IPmsRoomService;

/**
 * 房间管理Service业务层实现类
 * 负责处理房屋及房间管理的业务逻辑操作
 * 
 * @author ruoyi
 */
@Service
public class PmsRoomServiceImpl implements IPmsRoomService 
{
    /** 房间管理Mapper数据访问接口 */
    @Autowired
    private PmsRoomMapper pmsRoomMapper;

    /**
     * 根据房间ID查询房间详细信息
     * 
     * @param roomId 房间管理主键ID
     * @return 房间管理实体对象
     */
    @Override
    public PmsRoom selectPmsRoomByRoomId(Long roomId) {
        // 调用Mapper接口根据主键查询单条房间记录
        return pmsRoomMapper.selectPmsRoomByRoomId(roomId);
    }

    /**
     * 查询符合条件的房间管理列表数据
     * 
     * @param pmsRoom 包含查询条件的房间管理实体
     * @return 房间管理对象集合
     */
    @Override
    public List<PmsRoom> selectPmsRoomList(PmsRoom pmsRoom) {
        // 调用Mapper接口根据条件查询房间列表
        return pmsRoomMapper.selectPmsRoomList(pmsRoom);
    }

    /**
     * 新增房间管理记录
     * 
     * @param pmsRoom 待新增的房间管理实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsRoom(PmsRoom pmsRoom) {
        // 自动设置记录的创建时间为当前时间
        pmsRoom.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入房间数据操作
        return pmsRoomMapper.insertPmsRoom(pmsRoom);
    }

    /**
     * 修改房间管理记录
     * 
     * @param pmsRoom 包含修改信息的房间管理实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsRoom(PmsRoom pmsRoom) {
        // 自动设置记录的更新时间为当前时间
        pmsRoom.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新房间数据操作
        return pmsRoomMapper.updatePmsRoom(pmsRoom);
    }

    /**
     * 批量删除房间管理记录
     * 
     * @param roomIds 需要批量删除的房间管理主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsRoomByRoomIds(Long[] roomIds) {
        // 调用Mapper接口根据主键数组批量删除房间记录
        return pmsRoomMapper.deletePmsRoomByRoomIds(roomIds);
    }

    /**
     * 根据房间ID单条删除房间管理信息
     * 
     * @param roomId 待删除的房间管理主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsRoomByRoomId(Long roomId) {
        // 调用Mapper接口根据主键删除单条房间记录
        return pmsRoomMapper.deletePmsRoomByRoomId(roomId);
    }
}
