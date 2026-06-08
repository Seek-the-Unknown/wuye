package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.ISysDictDataService;

/**
 * 字典数据 业务层处理实现类
 * 负责字典数据的增删改查以及与Redis缓存的同步处理
 * 
 * @author ruoyi
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService
{
    /** 字典数据Mapper接口，用于操作字典数据表 */
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData 字典数据信息，包含字典名称、字典类型、状态等查询条件
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData)
    {
        // 调用Mapper接口根据条件查询字典数据列表并返回
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型（例如：sys_user_sex）
     * @param dictValue 字典键值（例如：0, 1）
     * @return 字典标签（例如：男, 女）
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue)
    {
        // 调用Mapper接口查询对应的字典标签字符串并返回
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode 字典数据ID，主键
     * @return 字典数据实体对象
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode)
    {
        // 根据主键ID调用Mapper查询具体的字典数据对象
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据ID数组
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes)
    {
        // 遍历所有需要删除的字典数据ID
        for (Long dictCode : dictCodes)
        {
            // 首先查询出该字典数据，以便获取它的字典类型(dictType)
            SysDictData data = selectDictDataById(dictCode);
            // 调用Mapper接口根据ID删除该字典数据
            dictDataMapper.deleteDictDataById(dictCode);
            // 根据字典类型查询该类型下剩余的所有字典数据
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            // 更新Redis缓存：用最新的字典数据列表覆盖旧缓存，保证缓存一致性
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }

    /**
     * 新增保存字典数据信息
     * 
     * @param data 字典数据信息，包含字典类型、标签、键值等实体信息
     * @return 结果，大于0表示插入成功
     */
    @Override
    public int insertDictData(SysDictData data)
    {
        // 调用Mapper接口将字典数据插入数据库表
        int row = dictDataMapper.insertDictData(data);
        // 如果插入成功（受影响行数大于0）
        if (row > 0)
        {
            // 查询该字典类型下的所有字典数据（包括刚刚新增的）
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            // 更新Redis缓存，将最新的字典数据列表同步到缓存中
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        // 返回受影响的行数
        return row;
    }

    /**
     * 修改保存字典数据信息
     * 
     * @param data 字典数据信息，包含修改后的标签、键值等
     * @return 结果，大于0表示修改成功
     */
    @Override
    public int updateDictData(SysDictData data)
    {
        // 调用Mapper接口更新数据库中的字典数据
        int row = dictDataMapper.updateDictData(data);
        // 如果更新成功（受影响行数大于0）
        if (row > 0)
        {
            // 获取该字典类型下的所有最新字典数据
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            // 更新Redis缓存，将修改后的字典数据列表同步到缓存中
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        // 返回受影响的行数
        return row;
    }
}
