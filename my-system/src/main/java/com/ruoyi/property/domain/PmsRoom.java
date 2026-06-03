package com.ruoyi.property.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class PmsRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long roomId;
    @Excel(name = "所属楼宇ID")
    private Long buildingId;
    @Excel(name = "房屋编号")
    private String roomCode;
    @Excel(name = "房屋名称")
    private String roomName;
    @Excel(name = "所在楼层")
    private Integer floorNum;
    @Excel(name = "建筑面积")
    private BigDecimal constructionArea;
    @Excel(name = "使用面积")
    private BigDecimal useArea;
    @Excel(name = "房屋状态")
    private String status;
    private String delFlag;
    @Excel(name = "业主ID")
    private Long ownerId;
    @Excel(name = "业主姓名")
    private String ownerName;

    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerName() { return ownerName; }

    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getRoomId() { return roomId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    public Long getBuildingId() { return buildingId; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public String getRoomCode() { return roomCode; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getRoomName() { return roomName; }
    public void setFloorNum(Integer floorNum) { this.floorNum = floorNum; }
    public Integer getFloorNum() { return floorNum; }
    public void setConstructionArea(BigDecimal constructionArea) { this.constructionArea = constructionArea; }
    public BigDecimal getConstructionArea() { return constructionArea; }
    public void setUseArea(BigDecimal useArea) { this.useArea = useArea; }
    public BigDecimal getUseArea() { return useArea; }
    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDelFlag() { return delFlag; }
}
