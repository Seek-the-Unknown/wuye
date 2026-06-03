package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsFeeType;

public interface IPmsFeeTypeService {
    PmsFeeType selectPmsFeeTypeByFeeTypeId(Long feeTypeId);
    List<PmsFeeType> selectPmsFeeTypeList(PmsFeeType pmsFeeType);
    int insertPmsFeeType(PmsFeeType pmsFeeType);
    int updatePmsFeeType(PmsFeeType pmsFeeType);
    int deletePmsFeeTypeByFeeTypeIds(Long[] feeTypeIds);
    int deletePmsFeeTypeByFeeTypeId(Long feeTypeId);
}
