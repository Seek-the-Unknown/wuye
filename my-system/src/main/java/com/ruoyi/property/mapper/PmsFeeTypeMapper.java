package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsFeeType;

public interface PmsFeeTypeMapper {
    PmsFeeType selectPmsFeeTypeByFeeTypeId(Long feeTypeId);
    List<PmsFeeType> selectPmsFeeTypeList(PmsFeeType pmsFeeType);
    int insertPmsFeeType(PmsFeeType pmsFeeType);
    int updatePmsFeeType(PmsFeeType pmsFeeType);
    int deletePmsFeeTypeByFeeTypeId(Long feeTypeId);
    int deletePmsFeeTypeByFeeTypeIds(Long[] feeTypeIds);
}
