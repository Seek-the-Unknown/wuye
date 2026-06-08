package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsFeeRecordMapper;
import com.ruoyi.property.domain.PmsFeeRecord;
import com.ruoyi.property.service.IPmsFeeRecordService;

/**
 * 费用收取记录Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class PmsFeeRecordServiceImpl implements IPmsFeeRecordService {
    @Autowired
    private PmsFeeRecordMapper pmsFeeRecordMapper;

    /**
     * 查询费用收取记录
     * 
     * @param recordId 费用收取记录主键
     * @return 费用收取记录
     */
    @Override
    public PmsFeeRecord selectPmsFeeRecordByRecordId(Long recordId) {
        return pmsFeeRecordMapper.selectPmsFeeRecordByRecordId(recordId);
    }

    /**
     * 查询费用收取记录列表
     * 
     * @param pmsFeeRecord 费用收取记录
     * @return 费用收取记录集合
     */
    @Override
    public List<PmsFeeRecord> selectPmsFeeRecordList(PmsFeeRecord pmsFeeRecord) {
        return pmsFeeRecordMapper.selectPmsFeeRecordList(pmsFeeRecord);
    }

    /**
     * 新增费用收取记录
     * 
     * @param pmsFeeRecord 费用收取记录
     * @return 结果
     */
    @Override
    public int insertPmsFeeRecord(PmsFeeRecord pmsFeeRecord) {
        // 设置创建时间
        pmsFeeRecord.setCreateTime(DateUtils.getNowDate());
        return pmsFeeRecordMapper.insertPmsFeeRecord(pmsFeeRecord);
    }

    /**
     * 修改费用收取记录
     * 
     * @param pmsFeeRecord 费用收取记录
     * @return 结果
     */
    @Override
    public int updatePmsFeeRecord(PmsFeeRecord pmsFeeRecord) {
        // 设置更新时间
        pmsFeeRecord.setUpdateTime(DateUtils.getNowDate());
        return pmsFeeRecordMapper.updatePmsFeeRecord(pmsFeeRecord);
    }

    /**
     * 批量删除费用收取记录
     * 
     * @param recordIds 需要删除的费用收取记录主键集合
     * @return 结果
     */
    @Override
    public int deletePmsFeeRecordByRecordIds(Long[] recordIds) {
        return pmsFeeRecordMapper.deletePmsFeeRecordByRecordIds(recordIds);
    }

    /**
     * 删除费用收取记录信息
     * 
     * @param recordId 费用收取记录主键
     * @return 结果
     */
    @Override
    public int deletePmsFeeRecordByRecordId(Long recordId) {
        return pmsFeeRecordMapper.deletePmsFeeRecordByRecordId(recordId);
    }
}
