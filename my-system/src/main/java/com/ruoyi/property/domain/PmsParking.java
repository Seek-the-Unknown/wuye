package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 停车位管理对象 PmsParking
 * 
 * 用于表示物业管理系统中的停车位信息实体类
 */
public class PmsParking extends BaseEntity {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 停车位ID，主键 */
    private Long parkingId;

    /** 所属小区ID */
    @Excel(name = "小区ID")
    private Long communityId;

    /** 车位编号（如：A区-001） */
    @Excel(name = "车位编号")
    private String parkingCode;

    /** 车位类型 (0=地上,1=地下,2=立体) */
    @Excel(name = "车位类型", readConverterExp = "0=地上,1=地下,2=立体")
    private String parkingType;

    /** 绑定的业主ID */
    private Long ownerId;

    /** 绑定的车牌号 */
    @Excel(name = "车牌号")
    private String plateNumber;

    /** 绑定状态 (0=空闲,1=已绑定) */
    @Excel(name = "绑定状态", readConverterExp = "0=空闲,1=已绑定")
    private String bindStatus;

    /** 状态 (0=正常,1=停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 (0代表存在 2代表删除) */
    private String delFlag;

    /** 关联字段（显示用）：所属小区名称 */
    // 关联字段
    private String communityName;
    
    /** 关联字段（显示用）：绑定的业主姓名 */
    private String ownerName;

    /** 获取车位ID @return 车位ID */
    public Long getParkingId() { return parkingId; }
    
    /** 设置车位ID @param parkingId 车位ID */
    public void setParkingId(Long parkingId) { this.parkingId = parkingId; }
    
    /** 获取小区ID @return 小区ID */
    public Long getCommunityId() { return communityId; }
    
    /** 设置小区ID @param communityId 小区ID */
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    
    /** 获取车位编号 @return 车位编号 */
    public String getParkingCode() { return parkingCode; }
    
    /** 设置车位编号 @param parkingCode 车位编号 */
    public void setParkingCode(String parkingCode) { this.parkingCode = parkingCode; }
    
    /** 获取车位类型 @return 车位类型 */
    public String getParkingType() { return parkingType; }
    
    /** 设置车位类型 @param parkingType 车位类型 */
    public void setParkingType(String parkingType) { this.parkingType = parkingType; }
    
    /** 获取业主ID @return 业主ID */
    public Long getOwnerId() { return ownerId; }
    
    /** 设置业主ID @param ownerId 业主ID */
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    /** 获取车牌号 @return 车牌号 */
    public String getPlateNumber() { return plateNumber; }
    
    /** 设置车牌号 @param plateNumber 车牌号 */
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    
    /** 获取绑定状态 @return 绑定状态 */
    public String getBindStatus() { return bindStatus; }
    
    /** 设置绑定状态 @param bindStatus 绑定状态 */
    public void setBindStatus(String bindStatus) { this.bindStatus = bindStatus; }
    
    /** 获取状态 @return 状态 */
    public String getStatus() { return status; }
    
    /** 设置状态 @param status 状态 */
    public void setStatus(String status) { this.status = status; }
    
    /** 获取删除标志 @return 删除标志 */
    public String getDelFlag() { return delFlag; }
    
    /** 设置删除标志 @param delFlag 删除标志 */
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    
    /** 获取关联的小区名称 @return 小区名称 */
    public String getCommunityName() { return communityName; }
    
    /** 设置关联的小区名称 @param communityName 小区名称 */
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    
    /** 获取关联的业主姓名 @return 业主姓名 */
    public String getOwnerName() { return ownerName; }
    
    /** 设置关联的业主姓名 @param ownerName 业主姓名 */
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("parkingId", getParkingId())
            .append("communityId", getCommunityId())
            .append("parkingCode", getParkingCode())
            .append("parkingType", getParkingType())
            .append("plateNumber", getPlateNumber())
            .append("bindStatus", getBindStatus())
            .append("status", getStatus())
            .toString();
    }
}
