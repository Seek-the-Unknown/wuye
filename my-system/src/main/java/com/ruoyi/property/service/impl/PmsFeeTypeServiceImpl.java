package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsFeeTypeMapper;
import com.ruoyi.property.domain.PmsFeeType;
import com.ruoyi.property.service.IPmsFeeTypeService;

@Service
public class PmsFeeTypeServiceImpl implements IPmsFeeTypeService {
    @Autowired
    private PmsFeeTypeMapper pmsFeeTypeMapper;

    @Override
    public PmsFeeType selectPmsFeeTypeByFeeTypeId(Long feeTypeId) {
        return pmsFeeTypeMapper.selectPmsFeeTypeByFeeTypeId(feeTypeId);
    }

    @Override
    public List<PmsFeeType> selectPmsFeeTypeList(PmsFeeType pmsFeeType) {
        return pmsFeeTypeMapper.selectPmsFeeTypeList(pmsFeeType);
    }

    @Override
    public int insertPmsFeeType(PmsFeeType pmsFeeType) {
        pmsFeeType.setCreateTime(DateUtils.getNowDate());
        return pmsFeeTypeMapper.insertPmsFeeType(pmsFeeType);
    }

    @Override
    public int updatePmsFeeType(PmsFeeType pmsFeeType) {
        pmsFeeType.setUpdateTime(DateUtils.getNowDate());
        return pmsFeeTypeMapper.updatePmsFeeType(pmsFeeType);
    }

    @Override
    public int deletePmsFeeTypeByFeeTypeIds(Long[] feeTypeIds) {
        return pmsFeeTypeMapper.deletePmsFeeTypeByFeeTypeIds(feeTypeIds);
    }

    @Override
    public int deletePmsFeeTypeByFeeTypeId(Long feeTypeId) {
        return pmsFeeTypeMapper.deletePmsFeeTypeByFeeTypeId(feeTypeId);
    }
}
