package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsRoom;

/**
 * 房屋管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsRoomMapper 
{
    /**
     * 查询房屋管理
     * 
     * @param roomId 房屋管理主键
     * @return 房屋管理
     */
    public PmsRoom selectPmsRoomByRoomId(Long roomId);

    /**
     * 查询房屋管理列表
     * 
     * @param pmsRoom 房屋管理
     * @return 房屋管理集合
     */
    public List<PmsRoom> selectPmsRoomList(PmsRoom pmsRoom);

    /**
     * 新增房屋管理
     * 
     * @param pmsRoom 房屋管理
     * @return 结果
     */
    public int insertPmsRoom(PmsRoom pmsRoom);

    /**
     * 修改房屋管理
     * 
     * @param pmsRoom 房屋管理
     * @return 结果
     */
    public int updatePmsRoom(PmsRoom pmsRoom);

    /**
     * 删除房屋管理
     * 
     * @param roomId 房屋管理主键
     * @return 结果
     */
    public int deletePmsRoomByRoomId(Long roomId);

    /**
     * 批量删除房屋管理
     * 
     * @param roomIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsRoomByRoomIds(Long[] roomIds);
}
