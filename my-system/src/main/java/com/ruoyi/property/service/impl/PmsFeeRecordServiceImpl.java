package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsFeeRecordMapper;
import com.ruoyi.property.domain.PmsFeeRecord;
import com.ruoyi.property.service.IPmsFeeRecordService;

@Service
public class PmsFeeRecordServiceImpl implements IPmsFeeRecordService {
    @Autowired
    private PmsFeeRecordMapper pmsFeeRecordMapper;

    @Override
    public PmsFeeRecord selectPmsFeeRecordByRecordId(Long recordId) {
        return pmsFeeRecordMapper.selectPmsFeeRecordByRecordId(recordId);
    }

    @Override
    public List<PmsFeeRecord> selectPmsFeeRecordList(PmsFeeRecord pmsFeeRecord) {
        return pmsFeeRecordMapper.selectPmsFeeRecordList(pmsFeeRecord);
    }

    @Override
    public int insertPmsFeeRecord(PmsFeeRecord pmsFeeRecord) {
        pmsFeeRecord.setCreateTime(DateUtils.getNowDate());
        return pmsFeeRecordMapper.insertPmsFeeRecord(pmsFeeRecord);
    }

    @Override
    public int updatePmsFeeRecord(PmsFeeRecord pmsFeeRecord) {
        pmsFeeRecord.setUpdateTime(DateUtils.getNowDate());
        return pmsFeeRecordMapper.updatePmsFeeRecord(pmsFeeRecord);
    }

    @Override
    public int deletePmsFeeRecordByRecordIds(Long[] recordIds) {
        return pmsFeeRecordMapper.deletePmsFeeRecordByRecordIds(recordIds);
    }

    @Override
    public int deletePmsFeeRecordByRecordId(Long recordId) {
        return pmsFeeRecordMapper.deletePmsFeeRecordByRecordId(recordId);
    }
}
