package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsFeeRecord;

public interface PmsFeeRecordMapper {
    PmsFeeRecord selectPmsFeeRecordByRecordId(Long recordId);
    List<PmsFeeRecord> selectPmsFeeRecordList(PmsFeeRecord pmsFeeRecord);
    int insertPmsFeeRecord(PmsFeeRecord pmsFeeRecord);
    int updatePmsFeeRecord(PmsFeeRecord pmsFeeRecord);
    int deletePmsFeeRecordByRecordId(Long recordId);
    int deletePmsFeeRecordByRecordIds(Long[] recordIds);
}
