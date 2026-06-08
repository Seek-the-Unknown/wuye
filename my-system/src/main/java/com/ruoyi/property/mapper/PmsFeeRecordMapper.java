package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsFeeRecord;

/**
 * 费用记录管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsFeeRecordMapper {
    /**
     * 查询费用记录管理
     * 
     * @param recordId 费用记录管理主键
     * @return 费用记录管理
     */
    public PmsFeeRecord selectPmsFeeRecordByRecordId(Long recordId);

    /**
     * 查询费用记录管理列表
     * 
     * @param pmsFeeRecord 费用记录管理
     * @return 费用记录管理集合
     */
    public List<PmsFeeRecord> selectPmsFeeRecordList(PmsFeeRecord pmsFeeRecord);

    /**
     * 新增费用记录管理
     * 
     * @param pmsFeeRecord 费用记录管理
     * @return 结果
     */
    public int insertPmsFeeRecord(PmsFeeRecord pmsFeeRecord);

    /**
     * 修改费用记录管理
     * 
     * @param pmsFeeRecord 费用记录管理
     * @return 结果
     */
    public int updatePmsFeeRecord(PmsFeeRecord pmsFeeRecord);

    /**
     * 删除费用记录管理
     * 
     * @param recordId 费用记录管理主键
     * @return 结果
     */
    public int deletePmsFeeRecordByRecordId(Long recordId);

    /**
     * 批量删除费用记录管理
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsFeeRecordByRecordIds(Long[] recordIds);
}
