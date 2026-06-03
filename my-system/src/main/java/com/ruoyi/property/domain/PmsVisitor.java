package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 访客管理 pms_visitor
 */
public class PmsVisitor extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long visitorId;

    @Excel(name = "小区ID")
    private Long communityId;

    @Excel(name = "访客姓名")
    private String visitorName;

    @Excel(name = "访客手机号")
    private String visitorPhone;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "被访房屋")
    private String visitRoom;

    private Long ownerId;

    @Excel(name = "来访事由")
    private String visitReason;

    @Excel(name = "来访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date visitTime;

    @Excel(name = "离开时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leaveTime;

    @Excel(name = "状态", readConverterExp = "0=待审核,1=已放行,2=已离开,3=拒绝")
    private String visitStatus;

    // 关联字段
    private String communityName;
    private String ownerName;

    public Long getVisitorId() { return visitorId; }
    public void setVisitorId(Long visitorId) { this.visitorId = visitorId; }
    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public String getVisitorPhone() { return visitorPhone; }
    public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getVisitRoom() { return visitRoom; }
    public void setVisitRoom(String visitRoom) { this.visitRoom = visitRoom; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getVisitReason() { return visitReason; }
    public void setVisitReason(String visitReason) { this.visitReason = visitReason; }
    public Date getVisitTime() { return visitTime; }
    public void setVisitTime(Date visitTime) { this.visitTime = visitTime; }
    public Date getLeaveTime() { return leaveTime; }
    public void setLeaveTime(Date leaveTime) { this.leaveTime = leaveTime; }
    public String getVisitStatus() { return visitStatus; }
    public void setVisitStatus(String visitStatus) { this.visitStatus = visitStatus; }
    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("visitorId", getVisitorId())
            .append("visitorName", getVisitorName())
            .append("visitorPhone", getVisitorPhone())
            .append("visitRoom", getVisitRoom())
            .append("visitStatus", getVisitStatus())
            .toString();
    }
}
