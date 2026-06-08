package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 小区管理对象 PmsCommunity
 * 
 * 用于表示物业管理系统中的小区基本信息实体类
 * @author ruoyi
 */
public class PmsCommunity extends BaseEntity
{
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 小区ID，主键 */
    private Long communityId;

    /** 小区名称 */
    @Excel(name = "小区名称")
    private String communityName;

    /** 小区编码，用于唯一标识小区 */
    @Excel(name = "小区编码")
    private String communityCode;

    /** 小区详细地址 */
    @Excel(name = "小区地址")
    private String address;

    /** 开发商名称 */
    @Excel(name = "开发商")
    private String developer;

    /** 物业公司名称 */
    @Excel(name = "物业公司")
    private String propertyCompany;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /**
     * 设置小区ID
     * @param communityId 小区ID
     */
    public void setCommunityId(Long communityId) 
    {
        this.communityId = communityId;
    }

    /**
     * 获取小区ID
     * @return 小区ID
     */
    public Long getCommunityId() 
    {
        return communityId;
    }

    /**
     * 设置小区名称
     * @param communityName 小区名称
     */
    public void setCommunityName(String communityName) 
    {
        this.communityName = communityName;
    }

    /**
     * 获取小区名称
     * @return 小区名称
     */
    public String getCommunityName() 
    {
        return communityName;
    }

    /**
     * 设置小区编码
     * @param communityCode 小区编码
     */
    public void setCommunityCode(String communityCode) 
    {
        this.communityCode = communityCode;
    }

    /**
     * 获取小区编码
     * @return 小区编码
     */
    public String getCommunityCode() 
    {
        return communityCode;
    }

    /**
     * 设置小区详细地址
     * @param address 小区地址
     */
    public void setAddress(String address) 
    {
        this.address = address;
    }

    /**
     * 获取小区详细地址
     * @return 小区地址
     */
    public String getAddress() 
    {
        return address;
    }

    /**
     * 设置开发商名称
     * @param developer 开发商
     */
    public void setDeveloper(String developer) 
    {
        this.developer = developer;
    }

    /**
     * 获取开发商名称
     * @return 开发商
     */
    public String getDeveloper() 
    {
        return developer;
    }

    /**
     * 设置物业公司名称
     * @param propertyCompany 物业公司
     */
    public void setPropertyCompany(String propertyCompany) 
    {
        this.propertyCompany = propertyCompany;
    }

    /**
     * 获取物业公司名称
     * @return 物业公司
     */
    public String getPropertyCompany() 
    {
        return propertyCompany;
    }

    /**
     * 设置状态
     * @param status 状态（0正常 1停用）
     */
    public void setStatus(String status) 
    {
        this.status = status;
    }

    /**
     * 获取状态
     * @return 状态（0正常 1停用）
     */
    public String getStatus() 
    {
        return status;
    }

    /**
     * 设置删除标志
     * @param delFlag 删除标志（0代表存在 2代表删除）
     */
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    /**
     * 获取删除标志
     * @return 删除标志（0代表存在 2代表删除）
     */
    public String getDelFlag() 
    {
        return delFlag;
    }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
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
