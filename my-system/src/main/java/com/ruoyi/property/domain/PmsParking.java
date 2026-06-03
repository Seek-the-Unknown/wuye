package com.ruoyi.property.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 停车位管理 pms_parking
 */
public class PmsParking extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long parkingId;

    @Excel(name = "小区ID")
    private Long communityId;

    @Excel(name = "车位编号")
    private String parkingCode;

    @Excel(name = "车位类型", readConverterExp = "0=地上,1=地下,2=立体")
    private String parkingType;

    private Long ownerId;

    @Excel(name = "车牌号")
    private String plateNumber;

    @Excel(name = "绑定状态", readConverterExp = "0=空闲,1=已绑定")
    private String bindStatus;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    // 关联字段
    private String communityName;
    private String ownerName;

    public Long getParkingId() { return parkingId; }
    public void setParkingId(Long parkingId) { this.parkingId = parkingId; }
    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
    public String getParkingCode() { return parkingCode; }
    public void setParkingCode(String parkingCode) { this.parkingCode = parkingCode; }
    public String getParkingType() { return parkingType; }
    public void setParkingType(String parkingType) { this.parkingType = parkingType; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getBindStatus() { return bindStatus; }
    public void setBindStatus(String bindStatus) { this.bindStatus = bindStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("parkingId", getParkingId())
            .append("communityId", getCommunityId())
            .append("parkingCode", getParkingCode())
            .append("parkingType", getParkingType())
            .append("plateNumber", getPlateNumber())
            .append("bindStatus", getBindStatus())
            .append("status", getStatus())
            .toString();
    }
}
