package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 公告通知对象 PmsNotice
 * 
 * 用于表示物业管理系统中的公告、通知等信息发布实体类
 */
public class PmsNotice extends BaseEntity {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 公告通知ID，主键 */
    private Long noticeId;

    /** 所属小区ID */
    private Long communityId;

    /** 公告标题 */
    @Excel(name = "公告标题")
    private String noticeTitle;

    /** 公告类型 (0=通知, 1=公告, 2=紧急) */
    @Excel(name = "公告类型", readConverterExp = "0=通知,1=公告,2=紧急")
    private String noticeType;

    /** 公告详细内容 */
    @Excel(name = "公告内容")
    private String noticeContent;

    /** 公告状态 (0=草稿, 1=已发布, 2=已下线) */
    @Excel(name = "状态", readConverterExp = "0=草稿,1=已发布,2=已下线")
    private String status;

    /** 公告发布时间 */
    private Date publishTime;

    /** 删除标志 (0代表存在 2代表删除) */
    private String delFlag;

    /** 关联字段（显示用）：所属小区名称 */
    // 关联字段
    private String communityName;

    /** 获取公告ID @return 公告ID */
    public Long getNoticeId() { return noticeId; }
    
    /** 设置公告ID @param noticeId 公告ID */
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }
    
    /** 获取小区ID @return 小区ID */
    public Long getCommunityId() { return communityId; }
    
    /** 设置小区ID @param communityId 小区ID */
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    
    /** 获取公告标题 @return 公告标题 */
    public String getNoticeTitle() { return noticeTitle; }
    
    /** 设置公告标题 @param noticeTitle 公告标题 */
    public void setNoticeTitle(String noticeTitle) { this.noticeTitle = noticeTitle; }
    
    /** 获取公告类型 @return 公告类型 */
    public String getNoticeType() { return noticeType; }
    
    /** 设置公告类型 @param noticeType 公告类型 */
    public void setNoticeType(String noticeType) { this.noticeType = noticeType; }
    
    /** 获取公告内容 @return 公告内容 */
    public String getNoticeContent() { return noticeContent; }
    
    /** 设置公告内容 @param noticeContent 公告内容 */
    public void setNoticeContent(String noticeContent) { this.noticeContent = noticeContent; }
    
    /** 获取状态 @return 状态 */
    public String getStatus() { return status; }
    
    /** 设置状态 @param status 状态 */
    public void setStatus(String status) { this.status = status; }
    
    /** 获取发布时间 @return 发布时间 */
    public Date getPublishTime() { return publishTime; }
    
    /** 设置发布时间 @param publishTime 发布时间 */
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
    
    /** 获取删除标志 @return 删除标志 */
    public String getDelFlag() { return delFlag; }
    
    /** 设置删除标志 @param delFlag 删除标志 */
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    
    /** 获取关联的小区名称 @return 小区名称 */
    public String getCommunityName() { return communityName; }
    
    /** 设置关联的小区名称 @param communityName 小区名称 */
    public void setCommunityName(String communityName) { this.communityName = communityName; }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("noticeId", getNoticeId())
            .append("noticeTitle", getNoticeTitle())
            .append("noticeType", getNoticeType())
            .append("status", getStatus())
            .toString();
    }
}
