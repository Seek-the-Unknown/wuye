package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 投诉建议对象 PmsComplaint
 * 
 * 用于表示物业管理系统中的投诉、建议或表扬记录的实体类
 */
public class PmsComplaint extends BaseEntity {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 投诉建议ID，主键 */
    private Long complaintId;

    /** 所属小区ID */
    @Excel(name = "小区ID")
    private Long communityId;

    /** 提交投诉建议的业主ID */
    @Excel(name = "业主ID")
    private Long ownerId;

    /** 投诉建议类型 (0=投诉,1=建议,2=表扬) */
    @Excel(name = "类型", readConverterExp = "0=投诉,1=建议,2=表扬")
    private String complaintType;

    /** 投诉建议标题 */
    @Excel(name = "标题")
    private String complaintTitle;

    /** 投诉建议内容 */
    @Excel(name = "内容")
    private String complaintContent;

    /** 处理状态 (0=待处理,1=处理中,2=已回复,3=已关闭) */
    @Excel(name = "处理状态", readConverterExp = "0=待处理,1=处理中,2=已回复,3=已关闭")
    private String handleStatus;

    /** 处理结果或回复内容 */
    private String handleResult;
    
    /** 处理人员用户名 */
    private String handleBy;

    /** 处理时间，格式：yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /** 关联字段（显示用）：小区名称 */
    // 关联字段
    private String communityName;
    
    /** 关联字段（显示用）：业主姓名 */
    private String ownerName;

    /** 获取投诉建议ID @return 投诉建议ID */
    public Long getComplaintId() { return complaintId; }
    
    /** 设置投诉建议ID @param complaintId 投诉建议ID */
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    
    /** 获取小区ID @return 小区ID */
    public Long getCommunityId() { return communityId; }
    
    /** 设置小区ID @param communityId 小区ID */
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    
    /** 获取业主ID @return 业主ID */
    public Long getOwnerId() { return ownerId; }
    
    /** 设置业主ID @param ownerId 业主ID */
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    /** 获取类型 @return 类型 */
    public String getComplaintType() { return complaintType; }
    
    /** 设置类型 @param complaintType 类型 */
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }
    
    /** 获取标题 @return 标题 */
    public String getComplaintTitle() { return complaintTitle; }
    
    /** 设置标题 @param complaintTitle 标题 */
    public void setComplaintTitle(String complaintTitle) { this.complaintTitle = complaintTitle; }
    
    /** 获取内容 @return 内容 */
    public String getComplaintContent() { return complaintContent; }
    
    /** 设置内容 @param complaintContent 内容 */
    public void setComplaintContent(String complaintContent) { this.complaintContent = complaintContent; }
    
    /** 获取处理状态 @return 处理状态 */
    public String getHandleStatus() { return handleStatus; }
    
    /** 设置处理状态 @param handleStatus 处理状态 */
    public void setHandleStatus(String handleStatus) { this.handleStatus = handleStatus; }
    
    /** 获取处理结果 @return 处理结果 */
    public String getHandleResult() { return handleResult; }
    
    /** 设置处理结果 @param handleResult 处理结果 */
    public void setHandleResult(String handleResult) { this.handleResult = handleResult; }
    
    /** 获取处理人 @return 处理人 */
    public String getHandleBy() { return handleBy; }
    
    /** 设置处理人 @param handleBy 处理人 */
    public void setHandleBy(String handleBy) { this.handleBy = handleBy; }
    
    /** 获取处理时间 @return 处理时间 */
    public Date getHandleTime() { return handleTime; }
    
    /** 设置处理时间 @param handleTime 处理时间 */
    public void setHandleTime(Date handleTime) { this.handleTime = handleTime; }
    
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
            .append("complaintId", getComplaintId())
            .append("complaintTitle", getComplaintTitle())
            .append("complaintType", getComplaintType())
            .append("handleStatus", getHandleStatus())
            .toString();
    }
}
