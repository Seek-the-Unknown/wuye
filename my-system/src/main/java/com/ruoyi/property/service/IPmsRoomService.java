package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsRoom;

/**
 * 房间管理Service接口
 * 
 * @author ruoyi
 */
public interface IPmsRoomService 
{
    /**
     * 查询房间管理
     * 
     * @param roomId 房间管理主键
     * @return 房间管理
     */
    public PmsRoom selectPmsRoomByRoomId(Long roomId);

    /**
     * 查询房间管理列表
     * 
     * @param pmsRoom 房间管理
     * @return 房间管理集合
     */
    public List<PmsRoom> selectPmsRoomList(PmsRoom pmsRoom);

    /**
     * 新增房间管理
     * 
     * @param pmsRoom 房间管理
     * @return 结果
     */
    public int insertPmsRoom(PmsRoom pmsRoom);

    /**
     * 修改房间管理
     * 
     * @param pmsRoom 房间管理
     * @return 结果
     */
    public int updatePmsRoom(PmsRoom pmsRoom);

    /**
     * 批量删除房间管理
     * 
     * @param roomIds 需要删除的房间管理主键集合
     * @return 结果
     */
    public int deletePmsRoomByRoomIds(Long[] roomIds);

    /**
     * 删除房间管理信息
     * 
     * @param roomId 房间管理主键
     * @return 结果
     */
    public int deletePmsRoomByRoomId(Long roomId);
}
