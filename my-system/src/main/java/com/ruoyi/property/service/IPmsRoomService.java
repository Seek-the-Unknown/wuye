package com.ruoyi.property.service;
import java.util.List;
import com.ruoyi.property.domain.PmsRoom;

public interface IPmsRoomService 
{
    public PmsRoom selectPmsRoomByRoomId(Long roomId);
    public List<PmsRoom> selectPmsRoomList(PmsRoom pmsRoom);
    public int insertPmsRoom(PmsRoom pmsRoom);
    public int updatePmsRoom(PmsRoom pmsRoom);
    public int deletePmsRoomByRoomIds(Long[] roomIds);
    public int deletePmsRoomByRoomId(Long roomId);
}
