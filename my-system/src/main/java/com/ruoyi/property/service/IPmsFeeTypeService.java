package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsFeeType;

/**
 * 费用类型Service接口
 * 
 * @author ruoyi
 */
public interface IPmsFeeTypeService {
    /**
     * 查询费用类型
     * 
     * @param feeTypeId 费用类型主键
     * @return 费用类型
     */
    PmsFeeType selectPmsFeeTypeByFeeTypeId(Long feeTypeId);

    /**
     * 查询费用类型列表
     * 
     * @param pmsFeeType 费用类型
     * @return 费用类型集合
     */
    List<PmsFeeType> selectPmsFeeTypeList(PmsFeeType pmsFeeType);

    /**
     * 新增费用类型
     * 
     * @param pmsFeeType 费用类型
     * @return 结果
     */
    int insertPmsFeeType(PmsFeeType pmsFeeType);

    /**
     * 修改费用类型
     * 
     * @param pmsFeeType 费用类型
     * @return 结果
     */
    int updatePmsFeeType(PmsFeeType pmsFeeType);

    /**
     * 批量删除费用类型
     * 
     * @param feeTypeIds 需要删除的费用类型主键集合
     * @return 结果
     */
    int deletePmsFeeTypeByFeeTypeIds(Long[] feeTypeIds);

    /**
     * 删除费用类型信息
     * 
     * @param feeTypeId 费用类型主键
     * @return 结果
     */
    int deletePmsFeeTypeByFeeTypeId(Long feeTypeId);
}
