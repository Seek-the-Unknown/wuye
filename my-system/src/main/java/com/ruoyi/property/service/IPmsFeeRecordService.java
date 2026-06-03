package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsFeeRecord;

public interface IPmsFeeRecordService {
    PmsFeeRecord selectPmsFeeRecordByRecordId(Long recordId);
    List<PmsFeeRecord> selectPmsFeeRecordList(PmsFeeRecord pmsFeeRecord);
    int insertPmsFeeRecord(PmsFeeRecord pmsFeeRecord);
    int updatePmsFeeRecord(PmsFeeRecord pmsFeeRecord);
    int deletePmsFeeRecordByRecordIds(Long[] recordIds);
    int deletePmsFeeRecordByRecordId(Long recordId);
}
