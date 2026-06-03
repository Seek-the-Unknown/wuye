package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物业费用记录 pms_fee_record
 */
public class PmsFeeRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long recordId;

    @Excel(name = "小区ID")
    private Long communityId;

    @Excel(name = "房屋ID")
    private Long roomId;

    @Excel(name = "业主ID")
    private Long ownerId;

    @Excel(name = "费用类型ID")
    private Long feeTypeId;

    @Excel(name = "应缴金额")
    private BigDecimal feeAmount;

    @Excel(name = "已缴金额")
    private BigDecimal paidAmount;

    @Excel(name = "收费月份")
    private String feeMonth;

    @Excel(name = "缴费状态", readConverterExp = "0=未缴,1=已缴,2=部分缴")
    private String payStatus;

    private Date payTime;

    // 关联字段（非数据库列，用于展示）
    private String communityName;
    private String roomName;
    private String ownerName;
    private String typeName;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }
    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public String getFeeMonth() { return feeMonth; }
    public void setFeeMonth(String feeMonth) { this.feeMonth = feeMonth; }
    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("communityId", getCommunityId())
            .append("roomId", getRoomId())
            .append("ownerId", getOwnerId())
            .append("feeTypeId", getFeeTypeId())
            .append("feeAmount", getFeeAmount())
            .append("paidAmount", getPaidAmount())
            .append("feeMonth", getFeeMonth())
            .append("payStatus", getPayStatus())
            .toString();
    }
}
