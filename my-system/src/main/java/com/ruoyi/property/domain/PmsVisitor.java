package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 访客管理对象 PmsVisitor
 * 
 * 用于表示物业管理系统中的外来访客登记记录实体类
 */
public class PmsVisitor extends BaseEntity {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 访客记录ID，主键 */
    private Long visitorId;

    /** 所属小区ID */
    @Excel(name = "小区ID")
    private Long communityId;

    /** 访客姓名 */
    @Excel(name = "访客姓名")
    private String visitorName;

    /** 访客手机号 */
    @Excel(name = "访客手机号")
    private String visitorPhone;

    /** 访客身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 被访房屋编号或名称 */
    @Excel(name = "被访房屋")
    private String visitRoom;

    /** 被访业主ID */
    private Long ownerId;

    /** 来访事由 */
    @Excel(name = "来访事由")
    private String visitReason;

    /** 来访时间，格式：yyyy-MM-dd HH:mm:ss */
    @Excel(name = "来访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date visitTime;

    /** 离开时间，格式：yyyy-MM-dd HH:mm:ss */
    @Excel(name = "离开时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leaveTime;

    /** 访问状态 (0=待审核,1=已放行,2=已离开,3=拒绝) */
    @Excel(name = "状态", readConverterExp = "0=待审核,1=已放行,2=已离开,3=拒绝")
    private String visitStatus;

    /** 关联字段（显示用）：小区名称 */
    // 关联字段
    private String communityName;
    
    /** 关联字段（显示用）：业主姓名 */
    private String ownerName;

    /** 获取访客记录ID @return 访客记录ID */
    public Long getVisitorId() { return visitorId; }
    
    /** 设置访客记录ID @param visitorId 访客记录ID */
    public void setVisitorId(Long visitorId) { this.visitorId = visitorId; }
    
    /** 获取小区ID @return 小区ID */
    public Long getCommunityId() { return communityId; }
    
    /** 设置小区ID @param communityId 小区ID */
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    
    /** 获取访客姓名 @return 访客姓名 */
    public String getVisitorName() { return visitorName; }
    
    /** 设置访客姓名 @param visitorName 访客姓名 */
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    
    /** 获取访客手机号 @return 访客手机号 */
    public String getVisitorPhone() { return visitorPhone; }
    
    /** 设置访客手机号 @param visitorPhone 访客手机号 */
    public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }
    
    /** 获取身份证号 @return 身份证号 */
    public String getIdCard() { return idCard; }
    
    /** 设置身份证号 @param idCard 身份证号 */
    public void setIdCard(String idCard) { this.idCard = idCard; }
    
    /** 获取被访房屋 @return 被访房屋 */
    public String getVisitRoom() { return visitRoom; }
    
    /** 设置被访房屋 @param visitRoom 被访房屋 */
    public void setVisitRoom(String visitRoom) { this.visitRoom = visitRoom; }
    
    /** 获取业主ID @return 业主ID */
    public Long getOwnerId() { return ownerId; }
    
    /** 设置业主ID @param ownerId 业主ID */
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    /** 获取来访事由 @return 来访事由 */
    public String getVisitReason() { return visitReason; }
    
    /** 设置来访事由 @param visitReason 来访事由 */
    public void setVisitReason(String visitReason) { this.visitReason = visitReason; }
    
    /** 获取来访时间 @return 来访时间 */
    public Date getVisitTime() { return visitTime; }
    
    /** 设置来访时间 @param visitTime 来访时间 */
    public void setVisitTime(Date visitTime) { this.visitTime = visitTime; }
    
    /** 获取离开时间 @return 离开时间 */
    public Date getLeaveTime() { return leaveTime; }
    
    /** 设置离开时间 @param leaveTime 离开时间 */
    public void setLeaveTime(Date leaveTime) { this.leaveTime = leaveTime; }
    
    /** 获取访问状态 @return 访问状态 */
    public String getVisitStatus() { return visitStatus; }
    
    /** 设置访问状态 @param visitStatus 访问状态 */
    public void setVisitStatus(String visitStatus) { this.visitStatus = visitStatus; }
    
    /** 获取小区名称 @return 小区名称 */
    public String getCommunityName() { return communityName; }
    
    /** 设置小区名称 @param communityName 小区名称 */
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    
    /** 获取业主姓名 @return 业主姓名 */
    public String getOwnerName() { return ownerName; }
    
    /** 设置业主姓名 @param ownerName 业主姓名 */
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("visitorId", getVisitorId())
            .append("visitorName", getVisitorName())
            .append("visitorPhone", getVisitorPhone())
            .append("visitRoom", getVisitRoom())
            .append("visitStatus", getVisitStatus())
            .toString();
    }
}
