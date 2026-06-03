package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class PmsRepair extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long repairId;
    @Excel(name = "小区ID")
    private Long communityId;
    @Excel(name = "业主ID")
    private Long ownerId;
    @Excel(name = "报修标题")
    private String repairTitle;
    @Excel(name = "报修内容")
    private String repairContent;
    @Excel(name = "报修状态")
    private String repairStatus;

    private Long workerId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date assignTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    // 关联显示字段
    private String workerName;
    private String communityName;
    private String ownerName;

    public void setRepairId(Long repairId) { this.repairId = repairId; }
    public Long getRepairId() { return repairId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    public Long getCommunityId() { return communityId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Long getOwnerId() { return ownerId; }
    public void setRepairTitle(String repairTitle) { this.repairTitle = repairTitle; }
    public String getRepairTitle() { return repairTitle; }
    public void setRepairContent(String repairContent) { this.repairContent = repairContent; }
    public String getRepairContent() { return repairContent; }
    public void setRepairStatus(String repairStatus) { this.repairStatus = repairStatus; }
    public String getRepairStatus() { return repairStatus; }

    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    public Long getWorkerId() { return workerId; }
    public void setAssignTime(Date assignTime) { this.assignTime = assignTime; }
    public Date getAssignTime() { return assignTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    public Date getFinishTime() { return finishTime; }

    public void setWorkerName(String workerName) { this.workerName = workerName; }
    public String getWorkerName() { return workerName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    public String getCommunityName() { return communityName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerName() { return ownerName; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("repairId", getRepairId())
            .append("repairTitle", getRepairTitle())
            .append("repairStatus", getRepairStatus())
            .append("workerId", getWorkerId())
            .toString();
    }
}
