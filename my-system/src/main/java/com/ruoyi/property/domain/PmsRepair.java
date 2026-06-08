package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 报修对象 PmsRepair
 * 
 * 用于表示物业管理系统中的业主报修、维修记录实体类
 */
public class PmsRepair extends BaseEntity
{
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 报修记录ID，主键 */
    private Long repairId;

    /** 所属小区ID */
    @Excel(name = "小区ID")
    private Long communityId;

    /** 报修业主ID */
    @Excel(name = "业主ID")
    private Long ownerId;

    /** 报修标题 */
    @Excel(name = "报修标题")
    private String repairTitle;

    /** 报修详细内容 */
    @Excel(name = "报修内容")
    private String repairContent;

    /** 报修状态 (例如：0=待分配, 1=处理中, 2=已完成, 3=已取消) */
    @Excel(name = "报修状态")
    private String repairStatus;

    /** 维修工/处理人员ID */
    private Long workerId;

    /** 分配时间，格式：yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date assignTime;

    /** 完成时间，格式：yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    /** 关联字段（显示用）：维修人员姓名 */
    // 关联显示字段
    private String workerName;

    /** 关联字段（显示用）：小区名称 */
    private String communityName;

    /** 关联字段（显示用）：业主姓名 */
    private String ownerName;

    /** 设置报修ID @param repairId 报修ID */
    public void setRepairId(Long repairId) { this.repairId = repairId; }
    
    /** 获取报修ID @return 报修ID */
    public Long getRepairId() { return repairId; }
    
    /** 设置小区ID @param communityId 小区ID */
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    
    /** 获取小区ID @return 小区ID */
    public Long getCommunityId() { return communityId; }
    
    /** 设置业主ID @param ownerId 业主ID */
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    /** 获取业主ID @return 业主ID */
    public Long getOwnerId() { return ownerId; }
    
    /** 设置报修标题 @param repairTitle 报修标题 */
    public void setRepairTitle(String repairTitle) { this.repairTitle = repairTitle; }
    
    /** 获取报修标题 @return 报修标题 */
    public String getRepairTitle() { return repairTitle; }
    
    /** 设置报修内容 @param repairContent 报修内容 */
    public void setRepairContent(String repairContent) { this.repairContent = repairContent; }
    
    /** 获取报修内容 @return 报修内容 */
    public String getRepairContent() { return repairContent; }
    
    /** 设置报修状态 @param repairStatus 报修状态 */
    public void setRepairStatus(String repairStatus) { this.repairStatus = repairStatus; }
    
    /** 获取报修状态 @return 报修状态 */
    public String getRepairStatus() { return repairStatus; }

    /** 设置维修人员ID @param workerId 维修工ID */
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    
    /** 获取维修人员ID @return 维修工ID */
    public Long getWorkerId() { return workerId; }
    
    /** 设置分配时间 @param assignTime 分配时间 */
    public void setAssignTime(Date assignTime) { this.assignTime = assignTime; }
    
    /** 获取分配时间 @return 分配时间 */
    public Date getAssignTime() { return assignTime; }
    
    /** 设置完成时间 @param finishTime 完成时间 */
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    
    /** 获取完成时间 @return 完成时间 */
    public Date getFinishTime() { return finishTime; }

    /** 设置维修人员姓名 @param workerName 维修工姓名 */
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    
    /** 获取维修人员姓名 @return 维修工姓名 */
    public String getWorkerName() { return workerName; }
    
    /** 设置小区名称 @param communityName 小区名称 */
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    
    /** 获取小区名称 @return 小区名称 */
    public String getCommunityName() { return communityName; }
    
    /** 设置业主姓名 @param ownerName 业主姓名 */
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    /** 获取业主姓名 @return 业主姓名 */
    public String getOwnerName() { return ownerName; }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("repairId", getRepairId())
            .append("repairTitle", getRepairTitle())
            .append("repairStatus", getRepairStatus())
            .append("workerId", getWorkerId())
            .toString();
    }
}
