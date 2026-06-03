package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 投诉建议 pms_complaint
 */
public class PmsComplaint extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long complaintId;

    @Excel(name = "小区ID")
    private Long communityId;

    @Excel(name = "业主ID")
    private Long ownerId;

    @Excel(name = "类型", readConverterExp = "0=投诉,1=建议,2=表扬")
    private String complaintType;

    @Excel(name = "标题")
    private String complaintTitle;

    @Excel(name = "内容")
    private String complaintContent;

    @Excel(name = "处理状态", readConverterExp = "0=待处理,1=处理中,2=已回复,3=已关闭")
    private String handleStatus;

    private String handleResult;
    private String handleBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    // 关联字段
    private String communityName;
    private String ownerName;

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }
    public String getComplaintTitle() { return complaintTitle; }
    public void setComplaintTitle(String complaintTitle) { this.complaintTitle = complaintTitle; }
    public String getComplaintContent() { return complaintContent; }
    public void setComplaintContent(String complaintContent) { this.complaintContent = complaintContent; }
    public String getHandleStatus() { return handleStatus; }
    public void setHandleStatus(String handleStatus) { this.handleStatus = handleStatus; }
    public String getHandleResult() { return handleResult; }
    public void setHandleResult(String handleResult) { this.handleResult = handleResult; }
    public String getHandleBy() { return handleBy; }
    public void setHandleBy(String handleBy) { this.handleBy = handleBy; }
    public Date getHandleTime() { return handleTime; }
    public void setHandleTime(Date handleTime) { this.handleTime = handleTime; }
    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("complaintId", getComplaintId())
            .append("complaintTitle", getComplaintTitle())
            .append("complaintType", getComplaintType())
            .append("handleStatus", getHandleStatus())
            .toString();
    }
}
