package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsFeeType;

/**
 * 费用类型管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsFeeTypeMapper {
    /**
     * 查询费用类型管理
     * 
     * @param feeTypeId 费用类型管理主键
     * @return 费用类型管理
     */
    public PmsFeeType selectPmsFeeTypeByFeeTypeId(Long feeTypeId);

    /**
     * 查询费用类型管理列表
     * 
     * @param pmsFeeType 费用类型管理
     * @return 费用类型管理集合
     */
    public List<PmsFeeType> selectPmsFeeTypeList(PmsFeeType pmsFeeType);

    /**
     * 新增费用类型管理
     * 
     * @param pmsFeeType 费用类型管理
     * @return 结果
     */
    public int insertPmsFeeType(PmsFeeType pmsFeeType);

    /**
     * 修改费用类型管理
     * 
     * @param pmsFeeType 费用类型管理
     * @return 结果
     */
    public int updatePmsFeeType(PmsFeeType pmsFeeType);

    /**
     * 删除费用类型管理
     * 
     * @param feeTypeId 费用类型管理主键
     * @return 结果
     */
    public int deletePmsFeeTypeByFeeTypeId(Long feeTypeId);

    /**
     * 批量删除费用类型管理
     * 
     * @param feeTypeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsFeeTypeByFeeTypeIds(Long[] feeTypeIds);
}
