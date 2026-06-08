package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 物业费用类型对象 PmsFeeType
 * 
 * 用于表示物业管理系统中的不同费用类型实体类（如物业费、水费、电费等）
 */
public class PmsFeeType extends BaseEntity {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 费用类型ID，主键 */
    private Long feeTypeId;

    /** 费用类型名称（如：物业费、水费、停车费等） */
    @Excel(name = "费用类型名称")
    private String typeName;

    /** 费用类型编码 */
    @Excel(name = "费用类型编码")
    private String typeCode;

    /** 收费单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 计费单位（如：元/平米/月，元/吨等） */
    @Excel(name = "计费单位")
    private String unit;

    /** 状态 (0=正常, 1=停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 (0代表存在 2代表删除) */
    private String delFlag;

    /** 获取费用类型ID @return 费用类型ID */
    public Long getFeeTypeId() { return feeTypeId; }
    
    /** 设置费用类型ID @param feeTypeId 费用类型ID */
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }
    
    /** 获取费用类型名称 @return 费用类型名称 */
    public String getTypeName() { return typeName; }
    
    /** 设置费用类型名称 @param typeName 费用类型名称 */
    public void setTypeName(String typeName) { this.typeName = typeName; }
    
    /** 获取费用类型编码 @return 费用类型编码 */
    public String getTypeCode() { return typeCode; }
    
    /** 设置费用类型编码 @param typeCode 费用类型编码 */
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    
    /** 获取单价 @return 单价 */
    public BigDecimal getUnitPrice() { return unitPrice; }
    
    /** 设置单价 @param unitPrice 单价 */
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    /** 获取计费单位 @return 计费单位 */
    public String getUnit() { return unit; }
    
    /** 设置计费单位 @param unit 计费单位 */
    public void setUnit(String unit) { this.unit = unit; }
    
    /** 获取状态 @return 状态 */
    public String getStatus() { return status; }
    
    /** 设置状态 @param status 状态 */
    public void setStatus(String status) { this.status = status; }
    
    /** 获取删除标志 @return 删除标志 */
    public String getDelFlag() { return delFlag; }
    
    /** 设置删除标志 @param delFlag 删除标志 */
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    /**
     * 重写toString方法，返回对象的字符串表示
     * @return 包含各个字段信息的字符串
     */
    @Override
    public String toString() {
        // 使用ToStringBuilder构建多行样式的字符串输出
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("feeTypeId", getFeeTypeId())
            .append("typeName", getTypeName())
            .append("typeCode", getTypeCode())
            .append("unitPrice", getUnitPrice())
            .append("unit", getUnit())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}
