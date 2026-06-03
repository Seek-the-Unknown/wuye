package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 物业费用类型 pms_fee_type
 */
public class PmsFeeType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long feeTypeId;

    @Excel(name = "费用类型名称")
    private String typeName;

    @Excel(name = "费用类型编码")
    private String typeCode;

    @Excel(name = "单价")
    private BigDecimal unitPrice;

    @Excel(name = "计费单位")
    private String unit;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
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
