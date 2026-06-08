package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsFeeTypeMapper;
import com.ruoyi.property.domain.PmsFeeType;
import com.ruoyi.property.service.IPmsFeeTypeService;

/**
 * 费用类型Service业务层实现类
 * 负责处理各类费用类型管理的业务逻辑
 * 
 * @author ruoyi
 */
@Service
public class PmsFeeTypeServiceImpl implements IPmsFeeTypeService {
    
    /** 费用类型Mapper数据访问接口 */
    @Autowired
    private PmsFeeTypeMapper pmsFeeTypeMapper;

    /**
     * 根据费用类型ID查询详细信息
     * 
     * @param feeTypeId 费用类型主键ID
     * @return 费用类型实体对象
     */
    @Override
    public PmsFeeType selectPmsFeeTypeByFeeTypeId(Long feeTypeId) {
        // 调用Mapper接口根据主键查询费用类型记录
        return pmsFeeTypeMapper.selectPmsFeeTypeByFeeTypeId(feeTypeId);
    }

    /**
     * 查询符合条件的费用类型列表数据
     * 
     * @param pmsFeeType 包含查询条件的费用类型实体
     * @return 费用类型对象集合
     */
    @Override
    public List<PmsFeeType> selectPmsFeeTypeList(PmsFeeType pmsFeeType) {
        // 调用Mapper接口根据条件查询费用类型列表
        return pmsFeeTypeMapper.selectPmsFeeTypeList(pmsFeeType);
    }

    /**
     * 新增费用类型记录
     * 
     * @param pmsFeeType 待新增的费用类型实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsFeeType(PmsFeeType pmsFeeType) {
        // 自动设置记录的创建时间为当前时间
        pmsFeeType.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入操作
        return pmsFeeTypeMapper.insertPmsFeeType(pmsFeeType);
    }

    /**
     * 修改费用类型记录
     * 
     * @param pmsFeeType 包含修改信息的费用类型实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsFeeType(PmsFeeType pmsFeeType) {
        // 自动设置记录的更新时间为当前时间
        pmsFeeType.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新操作
        return pmsFeeTypeMapper.updatePmsFeeType(pmsFeeType);
    }

    /**
     * 批量删除费用类型记录
     * 
     * @param feeTypeIds 需要批量删除的费用类型主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsFeeTypeByFeeTypeIds(Long[] feeTypeIds) {
        // 调用Mapper接口根据主键数组批量删除记录
        return pmsFeeTypeMapper.deletePmsFeeTypeByFeeTypeIds(feeTypeIds);
    }

    /**
     * 根据费用类型ID单条删除信息
     * 
     * @param feeTypeId 待删除的费用类型主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsFeeTypeByFeeTypeId(Long feeTypeId) {
        // 调用Mapper接口根据主键删除单条记录
        return pmsFeeTypeMapper.deletePmsFeeTypeByFeeTypeId(feeTypeId);
    }
}
