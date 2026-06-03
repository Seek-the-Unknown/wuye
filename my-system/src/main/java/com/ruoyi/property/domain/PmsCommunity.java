package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 小区管理对象 pms_community
 * 
 * @author ruoyi
 */
public class PmsCommunity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 小区ID */
    private Long communityId;

    /** 小区名称 */
    @Excel(name = "小区名称")
    private String communityName;

    /** 小区编码 */
    @Excel(name = "小区编码")
    private String communityCode;

    /** 小区地址 */
    @Excel(name = "小区地址")
    private String address;

    /** 开发商 */
    @Excel(name = "开发商")
    private String developer;

    /** 物业公司 */
    @Excel(name = "物业公司")
    private String propertyCompany;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setCommunityId(Long communityId) 
    {
        this.communityId = communityId;
    }

    public Long getCommunityId() 
    {
        return communityId;
    }
    public void setCommunityName(String communityName) 
    {
        this.communityName = communityName;
    }

    public String getCommunityName() 
    {
        return communityName;
    }
    public void setCommunityCode(String communityCode) 
    {
        this.communityCode = communityCode;
    }

    public String getCommunityCode() 
    {
        return communityCode;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setDeveloper(String developer) 
    {
        this.developer = developer;
    }

    public String getDeveloper() 
    {
        return developer;
    }
    public void setPropertyCompany(String propertyCompany) 
    {
        this.propertyCompany = propertyCompany;
    }

    public String getPropertyCompany() 
    {
        return propertyCompany;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("communityId", getCommunityId())
            .append("communityName", getCommunityName())
            .append("communityCode", getCommunityCode())
            .append("address", getAddress())
            .append("developer", getDeveloper())
            .append("propertyCompany", getPropertyCompany())
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
