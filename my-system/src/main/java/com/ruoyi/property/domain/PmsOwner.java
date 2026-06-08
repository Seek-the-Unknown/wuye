package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业主管理对象 PmsOwner
 * 
 * 用于表示物业管理系统中的业主基本信息实体类
 * @author ruoyi
 */
public class PmsOwner extends BaseEntity
{
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 业主ID，主键 */
    private Long ownerId;

    /** 业主姓名 */
    @Excel(name = "业主姓名")
    private String ownerName;

    /** 业主联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 业主身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 性别（0男 1女 2未知） */
    @Excel(name = "性别", readConverterExp = "0=男,1=女,2=未知")
    private String gender;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /**
     * 设置业主ID
     * @param ownerId 业主ID
     */
    public void setOwnerId(Long ownerId) 
    {
        this.ownerId = ownerId;
    }

    /**
     * 获取业主ID
     * @return 业主ID
     */
    public Long getOwnerId() 
    {
        return ownerId;
    }

    /**
     * 设置业主姓名
     * @param ownerName 业主姓名
     */
    public void setOwnerName(String ownerName) 
    {
        this.ownerName = ownerName;
    }

    /**
     * 获取业主姓名
     * @return 业主姓名
     */
    public String getOwnerName() 
    {
        return ownerName;
    }

    /**
     * 设置联系电话
     * @param phone 联系电话
     */
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    /**
     * 获取联系电话
     * @return 联系电话
     */
    public String getPhone() 
    {
        return phone;
    }

    /**
     * 设置身份证号
     * @param idCard 身份证号
     */
    public void setIdCard(String idCard) 
    {
        this.idCard = idCard;
    }

    /**
     * 获取身份证号
     * @return 身份证号
     */
    public String getIdCard() 
    {
        return idCard;
    }

    /**
     * 设置性别
     * @param gender 性别（0男 1女 2未知）
     */
    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    /**
     * 获取性别
     * @return 性别（0男 1女 2未知）
     */
    public String getGender() 
    {
        return gender;
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
            .append("ownerId", getOwnerId())
            .append("ownerName", getOwnerName())
            .append("phone", getPhone())
            .append("idCard", getIdCard())
            .append("gender", getGender())
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
