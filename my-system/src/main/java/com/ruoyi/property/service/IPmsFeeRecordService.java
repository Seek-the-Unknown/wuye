package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsFeeRecord;

/**
 * 费用收取记录Service接口
 * 
 * @author ruoyi
 */
public interface IPmsFeeRecordService {
    /**
     * 查询费用收取记录
     * 
     * @param recordId 费用收取记录主键
     * @return 费用收取记录
     */
    PmsFeeRecord selectPmsFeeRecordByRecordId(Long recordId);

    /**
     * 查询费用收取记录列表
     * 
     * @param pmsFeeRecord 费用收取记录
     * @return 费用收取记录集合
     */
    List<PmsFeeRecord> selectPmsFeeRecordList(PmsFeeRecord pmsFeeRecord);

    /**
     * 新增费用收取记录
     * 
     * @param pmsFeeRecord 费用收取记录
     * @return 结果
     */
    int insertPmsFeeRecord(PmsFeeRecord pmsFeeRecord);

    /**
     * 修改费用收取记录
     * 
     * @param pmsFeeRecord 费用收取记录
     * @return 结果
     */
    int updatePmsFeeRecord(PmsFeeRecord pmsFeeRecord);

    /**
     * 批量删除费用收取记录
     * 
     * @param recordIds 需要删除的费用收取记录主键集合
     * @return 结果
     */
    int deletePmsFeeRecordByRecordIds(Long[] recordIds);

    /**
     * 删除费用收取记录信息
     * 
     * @param recordId 费用收取记录主键
     * @return 结果
     */
    int deletePmsFeeRecordByRecordId(Long recordId);
}
