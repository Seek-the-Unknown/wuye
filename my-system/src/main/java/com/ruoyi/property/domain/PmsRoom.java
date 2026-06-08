package com.ruoyi.property.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 房屋管理对象 PmsRoom
 * 
 * 用于表示物业管理系统中的房屋/单元信息实体类
 */
public class PmsRoom extends BaseEntity
{
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 房屋ID，主键 */
    private Long roomId;

    /** 所属楼宇ID */
    @Excel(name = "所属楼宇ID")
    private Long buildingId;

    /** 房屋编号（如：1001） */
    @Excel(name = "房屋编号")
    private String roomCode;

    /** 房屋名称 */
    @Excel(name = "房屋名称")
    private String roomName;

    /** 所在楼层 */
    @Excel(name = "所在楼层")
    private Integer floorNum;

    /** 建筑面积 */
    @Excel(name = "建筑面积")
    private BigDecimal constructionArea;

    /** 使用面积/套内面积 */
    @Excel(name = "使用面积")
    private BigDecimal useArea;

    /** 房屋状态 (例如：0=已售,1=未售,2=已入住等) */
    @Excel(name = "房屋状态")
    private String status;

    /** 删除标志 (0代表存在 2代表删除) */
    private String delFlag;

    /** 绑定的业主ID */
    @Excel(name = "业主ID")
    private Long ownerId;

    /** 绑定的业主姓名 */
    @Excel(name = "业主姓名")
    private String ownerName;

    /** 设置业主ID @param ownerId 业主ID */
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    /** 获取业主ID @return 业主ID */
    public Long getOwnerId() { return ownerId; }
    
    /** 设置业主姓名 @param ownerName 业主姓名 */
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    /** 获取业主姓名 @return 业主姓名 */
    public String getOwnerName() { return ownerName; }

    /** 设置房屋ID @param roomId 房屋ID */
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    /** 获取房屋ID @return 房屋ID */
    public Long getRoomId() { return roomId; }
    
    /** 设置所属楼宇ID @param buildingId 楼宇ID */
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    
    /** 获取所属楼宇ID @return 楼宇ID */
    public Long getBuildingId() { return buildingId; }
    
    /** 设置房屋编号 @param roomCode 房屋编号 */
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    
    /** 获取房屋编号 @return 房屋编号 */
    public String getRoomCode() { return roomCode; }
    
    /** 设置房屋名称 @param roomName 房屋名称 */
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    /** 获取房屋名称 @return 房屋名称 */
    public String getRoomName() { return roomName; }
    
    /** 设置所在楼层 @param floorNum 楼层数 */
    public void setFloorNum(Integer floorNum) { this.floorNum = floorNum; }
    
    /** 获取所在楼层 @return 楼层数 */
    public Integer getFloorNum() { return floorNum; }
    
    /** 设置建筑面积 @param constructionArea 建筑面积 */
    public void setConstructionArea(BigDecimal constructionArea) { this.constructionArea = constructionArea; }
    
    /** 获取建筑面积 @return 建筑面积 */
    public BigDecimal getConstructionArea() { return constructionArea; }
    
    /** 设置使用面积 @param useArea 使用面积 */
    public void setUseArea(BigDecimal useArea) { this.useArea = useArea; }
    
    /** 获取使用面积 @return 使用面积 */
    public BigDecimal getUseArea() { return useArea; }
    
    /** 设置状态 @param status 状态 */
    public void setStatus(String status) { this.status = status; }
    
    /** 获取状态 @return 状态 */
    public String getStatus() { return status; }
    
    /** 设置删除标志 @param delFlag 删除标志 */
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    
    /** 获取删除标志 @return 删除标志 */
    public String getDelFlag() { return delFlag; }
    
    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("roomId", getRoomId())
            .append("buildingId", getBuildingId())
            .append("roomCode", getRoomCode())
            .append("roomName", getRoomName())
            .append("ownerId", getOwnerId())
            .append("ownerName", getOwnerName())
            .toString();
    }
}
