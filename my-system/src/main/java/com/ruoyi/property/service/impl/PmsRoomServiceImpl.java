package com.ruoyi.property.service.impl;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsRoomMapper;
import com.ruoyi.property.domain.PmsRoom;
import com.ruoyi.property.service.IPmsRoomService;

@Service
public class PmsRoomServiceImpl implements IPmsRoomService 
{
    @Autowired
    private PmsRoomMapper pmsRoomMapper;

    @Override
    public PmsRoom selectPmsRoomByRoomId(Long roomId) { return pmsRoomMapper.selectPmsRoomByRoomId(roomId); }
    @Override
    public List<PmsRoom> selectPmsRoomList(PmsRoom pmsRoom) { return pmsRoomMapper.selectPmsRoomList(pmsRoom); }
    @Override
    public int insertPmsRoom(PmsRoom pmsRoom) { pmsRoom.setCreateTime(DateUtils.getNowDate()); return pmsRoomMapper.insertPmsRoom(pmsRoom); }
    @Override
    public int updatePmsRoom(PmsRoom pmsRoom) { pmsRoom.setUpdateTime(DateUtils.getNowDate()); return pmsRoomMapper.updatePmsRoom(pmsRoom); }
    @Override
    public int deletePmsRoomByRoomIds(Long[] roomIds) { return pmsRoomMapper.deletePmsRoomByRoomIds(roomIds); }
    @Override
    public int deletePmsRoomByRoomId(Long roomId) { return pmsRoomMapper.deletePmsRoomByRoomId(roomId); }
}
