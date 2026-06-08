package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 楼宇对象 PmsBuilding
 * 
 * 用于表示小区内的楼宇/楼栋信息实体类
 */
public class PmsBuilding extends BaseEntity
{
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 楼宇ID，主键 */
    private Long buildingId;

    /** 所属小区ID */
    @Excel(name = "所属小区ID")
    private Long communityId;

    /** 楼宇名称 */
    @Excel(name = "楼宇名称")
    private String buildingName;

    /** 楼宇编码 */
    @Excel(name = "楼宇编码")
    private String buildingCode;

    /** 总楼层数 */
    @Excel(name = "总楼层数")
    private Integer floors;

    /** 状态 (0=正常, 1=停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 (0代表存在 2代表删除) */
    private String delFlag;

    /** 关联字段（显示用）：所属小区名称 */
    // 关联字段（显示用）
    private String communityName;

    /**
     * 设置楼宇ID
     * @param buildingId 楼宇ID
     */
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    /**
     * 获取楼宇ID
     * @return 楼宇ID
     */
    public Long getBuildingId() { return buildingId; }

    /**
     * 设置所属小区ID
     * @param communityId 所属小区ID
     */
    public void setCommunityId(Long communityId) { this.communityId = communityId; }

    /**
     * 获取所属小区ID
     * @return 所属小区ID
     */
    public Long getCommunityId() { return communityId; }

    /**
     * 设置楼宇名称
     * @param buildingName 楼宇名称
     */
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    /**
     * 获取楼宇名称
     * @return 楼宇名称
     */
    public String getBuildingName() { return buildingName; }

    /**
     * 设置楼宇编码
     * @param buildingCode 楼宇编码
     */
    public void setBuildingCode(String buildingCode) { this.buildingCode = buildingCode; }

    /**
     * 获取楼宇编码
     * @return 楼宇编码
     */
    public String getBuildingCode() { return buildingCode; }

    /**
     * 设置总楼层数
     * @param floors 总楼层数
     */
    public void setFloors(Integer floors) { this.floors = floors; }

    /**
     * 获取总楼层数
     * @return 总楼层数
     */
    public Integer getFloors() { return floors; }

    /**
     * 设置状态
     * @param status 状态
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * 获取状态
     * @return 状态
     */
    public String getStatus() { return status; }

    /**
     * 设置删除标志
     * @param delFlag 删除标志
     */
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    /**
     * 获取删除标志
     * @return 删除标志
     */
    public String getDelFlag() { return delFlag; }

    /**
     * 获取关联的小区名称
     * @return 小区名称
     */
    public String getCommunityName() { return communityName; }

    /**
     * 设置关联的小区名称
     * @param communityName 小区名称
     */
    public void setCommunityName(String communityName) { this.communityName = communityName; }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("buildingId", getBuildingId())
            .append("communityId", getCommunityId())
            .append("buildingName", getBuildingName())
            .append("buildingCode", getBuildingCode())
            .append("floors", getFloors())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
