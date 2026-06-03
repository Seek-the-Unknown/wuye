package com.ruoyi.property.mapper;
import java.util.List;
import com.ruoyi.property.domain.PmsRoom;

public interface PmsRoomMapper 
{
    public PmsRoom selectPmsRoomByRoomId(Long roomId);
    public List<PmsRoom> selectPmsRoomList(PmsRoom pmsRoom);
    public int insertPmsRoom(PmsRoom pmsRoom);
    public int updatePmsRoom(PmsRoom pmsRoom);
    public int deletePmsRoomByRoomId(Long roomId);
    public int deletePmsRoomByRoomIds(Long[] roomIds);
}
