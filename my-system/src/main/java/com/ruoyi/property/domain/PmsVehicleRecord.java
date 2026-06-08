package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 车辆进出记录对象 PmsVehicleRecord
 * 
 * 用于表示物业管理系统中的车辆进出小区记录及费用信息的实体类
 */
public class PmsVehicleRecord extends BaseEntity {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 记录ID，主键 */
    private Long recordId;

    /** 所属小区ID */
    @Excel(name = "小区ID")
    private Long communityId;

    /** 关联的停车位ID（如果是业主车/月租车） */
    @Excel(name = "车位ID")
    private Long parkingId;

    /** 车辆号牌 */
    @Excel(name = "车牌号")
    private String plateNumber;

    /** 车辆入场时间，格式：yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "入场时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date entryTime;

    /** 车辆出场时间，格式：yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "出场时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date exitTime;

    /** 停车总时长（小时） */
    @Excel(name = "停车时长(小时)")
    private BigDecimal parkingDuration;

    /** 计费单价 */
    @Excel(name = "计费单价")
    private BigDecimal unitPrice;

    /** 应收停车费金额 */
    @Excel(name = "应收金额")
    private BigDecimal feeAmount;

    /** 实际收取停车费金额 */
    @Excel(name = "实收金额")
    private BigDecimal paidAmount;

    /** 支付状态 (0=未支付,1=已支付) */
    @Excel(name = "支付状态", readConverterExp = "0=未支付,1=已支付")
    private String payStatus;

    /** 实际支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 车辆类型 (0=临时车,1=月租车,2=业主车) */
    @Excel(name = "车辆类型", readConverterExp = "0=临时车,1=月租车,2=业主车")
    private String vehicleType;

    /** 入场抓拍图片地址 */
    private String entryImage;

    /** 出场抓拍图片地址 */
    private String exitImage;

    /** 删除标志 (0代表存在 2代表删除) */
    private String delFlag;

    /** 关联字段（显示用）：小区名称 */
    // 关联展示字段
    private String communityName;
    
    /** 关联字段（显示用）：业主姓名 */
    private String ownerName;
    
    /** 关联字段（显示用）：车位编号 */
    private String parkingCode;

    /** 获取记录ID @return 记录ID */
    public Long getRecordId() { return recordId; }
    
    /** 设置记录ID @param recordId 记录ID */
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    
    /** 获取小区ID @return 小区ID */
    public Long getCommunityId() { return communityId; }
    
    /** 设置小区ID @param communityId 小区ID */
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    
    /** 获取车位ID @return 车位ID */
    public Long getParkingId() { return parkingId; }
    
    /** 设置车位ID @param parkingId 车位ID */
    public void setParkingId(Long parkingId) { this.parkingId = parkingId; }
    
    /** 获取车牌号 @return 车牌号 */
    public String getPlateNumber() { return plateNumber; }
    
    /** 设置车牌号 @param plateNumber 车牌号 */
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    
    /** 获取入场时间 @return 入场时间 */
    public Date getEntryTime() { return entryTime; }
    
    /** 设置入场时间 @param entryTime 入场时间 */
    public void setEntryTime(Date entryTime) { this.entryTime = entryTime; }
    
    /** 获取出场时间 @return 出场时间 */
    public Date getExitTime() { return exitTime; }
    
    /** 设置出场时间 @param exitTime 出场时间 */
    public void setExitTime(Date exitTime) { this.exitTime = exitTime; }
    
    /** 获取停车时长 @return 停车时长 */
    public BigDecimal getParkingDuration() { return parkingDuration; }
    
    /** 设置停车时长 @param parkingDuration 停车时长 */
    public void setParkingDuration(BigDecimal parkingDuration) { this.parkingDuration = parkingDuration; }
    
    /** 获取计费单价 @return 计费单价 */
    public BigDecimal getUnitPrice() { return unitPrice; }
    
    /** 设置计费单价 @param unitPrice 计费单价 */
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    /** 获取应收金额 @return 应收金额 */
    public BigDecimal getFeeAmount() { return feeAmount; }
    
    /** 设置应收金额 @param feeAmount 应收金额 */
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }
    
    /** 获取实收金额 @return 实收金额 */
    public BigDecimal getPaidAmount() { return paidAmount; }
    
    /** 设置实收金额 @param paidAmount 实收金额 */
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    
    /** 获取支付状态 @return 支付状态 */
    public String getPayStatus() { return payStatus; }
    
    /** 设置支付状态 @param payStatus 支付状态 */
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    
    /** 获取支付时间 @return 支付时间 */
    public Date getPayTime() { return payTime; }
    
    /** 设置支付时间 @param payTime 支付时间 */
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    
    /** 获取车辆类型 @return 车辆类型 */
    public String getVehicleType() { return vehicleType; }
    
    /** 设置车辆类型 @param vehicleType 车辆类型 */
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    /** 获取入场图片 @return 入场图片 */
    public String getEntryImage() { return entryImage; }
    
    /** 设置入场图片 @param entryImage 入场图片 */
    public void setEntryImage(String entryImage) { this.entryImage = entryImage; }
    
    /** 获取出场图片 @return 出场图片 */
    public String getExitImage() { return exitImage; }
    
    /** 设置出场图片 @param exitImage 出场图片 */
    public void setExitImage(String exitImage) { this.exitImage = exitImage; }
    
    /** 获取删除标志 @return 删除标志 */
    public String getDelFlag() { return delFlag; }
    
    /** 设置删除标志 @param delFlag 删除标志 */
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    
    /** 获取小区名称 @return 小区名称 */
    public String getCommunityName() { return communityName; }
    
    /** 设置小区名称 @param communityName 小区名称 */
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    
    /** 获取业主姓名 @return 业主姓名 */
    public String getOwnerName() { return ownerName; }
    
    /** 设置业主姓名 @param ownerName 业主姓名 */
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    /** 获取车位编号 @return 车位编号 */
    public String getParkingCode() { return parkingCode; }
    
    /** 设置车位编号 @param parkingCode 车位编号 */
    public void setParkingCode(String parkingCode) { this.parkingCode = parkingCode; }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("plateNumber", getPlateNumber())
            .append("entryTime", getEntryTime())
            .append("exitTime", getExitTime())
            .append("feeAmount", getFeeAmount())
            .append("payStatus", getPayStatus())
            .toString();
    }
}
