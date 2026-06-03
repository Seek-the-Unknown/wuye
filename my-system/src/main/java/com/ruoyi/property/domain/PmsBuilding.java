package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class PmsBuilding extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long buildingId;
    @Excel(name = "所属小区ID")
    private Long communityId;
    @Excel(name = "楼宇名称")
    private String buildingName;
    @Excel(name = "楼宇编码")
    private String buildingCode;
    @Excel(name = "总楼层数")
    private Integer floors;
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
    private String delFlag;
    // 关联字段（显示用）
    private String communityName;

    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    public Long getBuildingId() { return buildingId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    public Long getCommunityId() { return communityId; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingCode(String buildingCode) { this.buildingCode = buildingCode; }
    public String getBuildingCode() { return buildingCode; }
    public void setFloors(Integer floors) { this.floors = floors; }
    public Integer getFloors() { return floors; }
    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDelFlag() { return delFlag; }
    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }

    @Override
    public String toString() {
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
