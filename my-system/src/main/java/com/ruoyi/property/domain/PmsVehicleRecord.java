package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 车辆进出记录 pms_vehicle_record
 */
public class PmsVehicleRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long recordId;

    @Excel(name = "小区ID")
    private Long communityId;

    @Excel(name = "车位ID")
    private Long parkingId;

    @Excel(name = "车牌号")
    private String plateNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "入场时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date entryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "出场时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date exitTime;

    @Excel(name = "停车时长(小时)")
    private BigDecimal parkingDuration;

    @Excel(name = "计费单价")
    private BigDecimal unitPrice;

    @Excel(name = "应收金额")
    private BigDecimal feeAmount;

    @Excel(name = "实收金额")
    private BigDecimal paidAmount;

    @Excel(name = "支付状态", readConverterExp = "0=未支付,1=已支付")
    private String payStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    @Excel(name = "车辆类型", readConverterExp = "0=临时车,1=月租车,2=业主车")
    private String vehicleType;

    private String entryImage;

    private String exitImage;

    private String delFlag;

    // 关联展示字段
    private String communityName;
    private String ownerName;
    private String parkingCode;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    public Long getParkingId() { return parkingId; }
    public void setParkingId(Long parkingId) { this.parkingId = parkingId; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public Date getEntryTime() { return entryTime; }
    public void setEntryTime(Date entryTime) { this.entryTime = entryTime; }
    public Date getExitTime() { return exitTime; }
    public void setExitTime(Date exitTime) { this.exitTime = exitTime; }
    public BigDecimal getParkingDuration() { return parkingDuration; }
    public void setParkingDuration(BigDecimal parkingDuration) { this.parkingDuration = parkingDuration; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getEntryImage() { return entryImage; }
    public void setEntryImage(String entryImage) { this.entryImage = entryImage; }
    public String getExitImage() { return exitImage; }
    public void setExitImage(String exitImage) { this.exitImage = exitImage; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getParkingCode() { return parkingCode; }
    public void setParkingCode(String parkingCode) { this.parkingCode = parkingCode; }

    @Override
    public String toString() {
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
