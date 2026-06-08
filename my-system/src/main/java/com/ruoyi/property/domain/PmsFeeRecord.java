package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物业费用记录对象 PmsFeeRecord
 * 
 * 用于表示物业管理系统中的费用缴纳记录实体类
 */
public class PmsFeeRecord extends BaseEntity {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 费用记录ID，主键 */
    private Long recordId;

    /** 所属小区ID */
    @Excel(name = "小区ID")
    private Long communityId;

    /** 关联的房屋ID */
    @Excel(name = "房屋ID")
    private Long roomId;

    /** 关联的业主ID */
    @Excel(name = "业主ID")
    private Long ownerId;

    /** 关联的费用类型ID */
    @Excel(name = "费用类型ID")
    private Long feeTypeId;

    /** 应缴金额 */
    @Excel(name = "应缴金额")
    private BigDecimal feeAmount;

    /** 实缴/已缴金额 */
    @Excel(name = "已缴金额")
    private BigDecimal paidAmount;

    /** 收费月份（如：2023-05） */
    @Excel(name = "收费月份")
    private String feeMonth;

    /** 缴费状态 (0=未缴,1=已缴,2=部分缴) */
    @Excel(name = "缴费状态", readConverterExp = "0=未缴,1=已缴,2=部分缴")
    private String payStatus;

    /** 实际缴费时间 */
    private Date payTime;

    /** 关联字段（显示用）：小区名称 */
    // 关联字段（非数据库列，用于展示）
    private String communityName;
    
    /** 关联字段（显示用）：房屋名称/编号 */
    private String roomName;
    
    /** 关联字段（显示用）：业主姓名 */
    private String ownerName;
    
    /** 关联字段（显示用）：费用类型名称 */
    private String typeName;

    /** 获取记录ID @return 记录ID */
    public Long getRecordId() { return recordId; }
    
    /** 设置记录ID @param recordId 记录ID */
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    
    /** 获取小区ID @return 小区ID */
    public Long getCommunityId() { return communityId; }
    
    /** 设置小区ID @param communityId 小区ID */
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    
    /** 获取房屋ID @return 房屋ID */
    public Long getRoomId() { return roomId; }
    
    /** 设置房屋ID @param roomId 房屋ID */
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    /** 获取业主ID @return 业主ID */
    public Long getOwnerId() { return ownerId; }
    
    /** 设置业主ID @param ownerId 业主ID */
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    /** 获取费用类型ID @return 费用类型ID */
    public Long getFeeTypeId() { return feeTypeId; }
    
    /** 设置费用类型ID @param feeTypeId 费用类型ID */
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }
    
    /** 获取应缴金额 @return 应缴金额 */
    public BigDecimal getFeeAmount() { return feeAmount; }
    
    /** 设置应缴金额 @param feeAmount 应缴金额 */
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }
    
    /** 获取已缴金额 @return 已缴金额 */
    public BigDecimal getPaidAmount() { return paidAmount; }
    
    /** 设置已缴金额 @param paidAmount 已缴金额 */
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    
    /** 获取收费月份 @return 收费月份 */
    public String getFeeMonth() { return feeMonth; }
    
    /** 设置收费月份 @param feeMonth 收费月份 */
    public void setFeeMonth(String feeMonth) { this.feeMonth = feeMonth; }
    
    /** 获取缴费状态 @return 缴费状态 */
    public String getPayStatus() { return payStatus; }
    
    /** 设置缴费状态 @param payStatus 缴费状态 */
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    
    /** 获取缴费时间 @return 缴费时间 */
    public Date getPayTime() { return payTime; }
    
    /** 设置缴费时间 @param payTime 缴费时间 */
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    
    /** 获取小区名称 @return 小区名称 */
    public String getCommunityName() { return communityName; }
    
    /** 设置小区名称 @param communityName 小区名称 */
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    
    /** 获取房屋名称 @return 房屋名称 */
    public String getRoomName() { return roomName; }
    
    /** 设置房屋名称 @param roomName 房屋名称 */
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    /** 获取业主姓名 @return 业主姓名 */
    public String getOwnerName() { return ownerName; }
    
    /** 设置业主姓名 @param ownerName 业主姓名 */
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    /** 获取费用类型名称 @return 费用类型名称 */
    public String getTypeName() { return typeName; }
    
    /** 设置费用类型名称 @param typeName 费用类型名称 */
    public void setTypeName(String typeName) { this.typeName = typeName; }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
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
