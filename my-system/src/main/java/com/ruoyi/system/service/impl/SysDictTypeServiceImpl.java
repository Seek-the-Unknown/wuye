package com.ruoyi.system.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import com.ruoyi.system.service.ISysDictTypeService;

/**
 * 字典类型 业务层处理实现类
 * 负责字典类型的增删改查、缓存初始化、缓存清理等业务逻辑
 * 
 * @author ruoyi
 */
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService
{
    /** 字典类型Mapper接口，用于操作字典类型表 */
    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    /** 字典数据Mapper接口，用于操作字典数据表 */
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     * 使用 @PostConstruct 注解保证在依赖注入完成后自动调用该方法
     */
    @PostConstruct
    public void init()
    {
        // 调用加载字典缓存的方法
        loadingDictCache();
    }

    /**
     * 根据条件分页查询字典类型
     * 
     * @param dictType 字典类型信息，包含字典名称、字典类型、状态等
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType)
    {
        // 调用Mapper根据条件查询字典类型列表并返回
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 查询所有字典类型
     * 
     * @return 所有字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll()
    {
        // 调用Mapper查询全部字典类型并返回
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型（例如：sys_user_sex）
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType)
    {
        // 首先从Redis缓存中获取该字典类型对应的数据列表
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        // 如果缓存中存在数据，直接返回缓存数据，减少数据库查询压力
        if (StringUtils.isNotEmpty(dictDatas))
        {
            return dictDatas;
        }
        // 如果缓存中没有数据，则调用Mapper从数据库中根据字典类型查询数据
        dictDatas = dictDataMapper.selectDictDataByType(dictType);
        // 如果数据库中查询到了数据
        if (StringUtils.isNotEmpty(dictDatas))
        {
            // 将查询到的数据放入Redis缓存中，方便下次查询
            DictUtils.setDictCache(dictType, dictDatas);
            // 返回从数据库查询到的数据
            return dictDatas;
        }
        // 如果数据库中也没有数据，返回null
        return null;
    }

    /**
     * 根据字典类型ID查询信息
     * 
     * @param dictId 字典类型ID，主键
     * @return 字典类型实体
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId)
    {
        // 根据主键ID调用Mapper查询字典类型详情
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 根据字典类型查询信息
     * 
     * @param dictType 字典类型字符串（唯一标识）
     * @return 字典类型实体
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType)
    {
        // 根据字典类型唯一字符串调用Mapper查询字典类型详情
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    /**
     * 批量删除字典类型信息
     * 
     * @param dictIds 需要删除的字典ID数组
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds)
    {
        // 遍历所有需要删除的字典类型ID
        for (Long dictId : dictIds)
        {
            // 查询该字典类型的详细信息
            SysDictType dictType = selectDictTypeById(dictId);
            // 校验该字典类型下是否还存在字典数据，如果存在(数量>0)则不允许删除
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0)
            {
                // 抛出业务异常，提示已分配不能删除
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            // 调用Mapper删除该字典类型
            dictTypeMapper.deleteDictTypeById(dictId);
            // 从Redis缓存中移除该字典类型的数据，保证缓存和数据库一致
            DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    /**
     * 加载字典缓存数据
     * 将数据库中正常状态的字典数据全部加载到Redis中
     */
    @Override
    public void loadingDictCache()
    {
        // 创建一个字典数据查询实体，用于条件查询
        SysDictData dictData = new SysDictData();
        // 设置状态为"0"（正常状态）
        dictData.setStatus("0");
        // 查询所有正常状态的字典数据，并通过Stream API根据字典类型(dictType)进行分组
        // 得到 Map 结构：键为字典类型，值为该类型下的字典数据列表
        Map<String, List<SysDictData>> dictDataMap = dictDataMapper.selectDictDataList(dictData).stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        // 遍历分组后的 Map
        for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet())
        {
            // 将每个类型对应的字典数据列表放入Redis缓存中
            // 在放入之前，使用Stream对列表按照字典排序字段(dictSort)进行升序排序，保证缓存中的数据是有序的
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictData::getDictSort)).collect(Collectors.toList()));
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache()
    {
        // 调用工具类清除Redis中的所有字典缓存
        DictUtils.clearDictCache();
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache()
    {
        // 先清空当前的字典缓存
        clearDictCache();
        // 重新从数据库加载字典数据到缓存中
        loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     * 
     * @param dict 字典类型信息
     * @return 结果，大于0表示插入成功
     */
    @Override
    public int insertDictType(SysDictType dict)
    {
        // 调用Mapper将新的字典类型插入数据库
        int row = dictTypeMapper.insertDictType(dict);
        // 如果插入成功
        if (row > 0)
        {
            // 初始化该字典类型的缓存为空
            DictUtils.setDictCache(dict.getDictType(), null);
        }
        // 返回受影响的行数
        return row;
    }

    /**
     * 修改保存字典类型信息
     * 如果修改了字典类型的类型字符串(dictType)，需要同步更新下属的所有字典数据
     * 
     * @param dict 字典类型信息
     * @return 结果，大于0表示修改成功
     */
    @Override
    @Transactional // 添加事务注解，保证字典类型和字典数据的修改原子性
    public int updateDictType(SysDictType dict)
    {
        // 先根据ID查询修改前的旧字典类型信息
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dict.getDictId());
        // 同步修改字典数据表中关联的字典类型字符串（将旧的dictType替换为新的dictType）
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        // 调用Mapper更新字典类型表本身的信息
        int row = dictTypeMapper.updateDictType(dict);
        // 如果更新成功
        if (row > 0)
        {
            // 获取更新后该字典类型下的所有最新字典数据
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dict.getDictType());
            // 更新Redis缓存，将最新的字典数据列表同步到缓存中
            DictUtils.setDictCache(dict.getDictType(), dictDatas);
        }
        // 返回受影响的行数
        return row;
    }

    /**
     * 校验字典类型名称是否唯一
     * 
     * @param dict 字典类型信息（包含ID和字典类型字符串）
     * @return 结果，返回"0"表示唯一，返回"1"表示不唯一
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dict)
    {
        // 获取传入的字典ID，如果是新增(ID为空)则默认为-1L
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        // 根据字典类型字符串从数据库中查询是否存在对应的字典类型
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        // 如果查询到了记录，并且查询到的记录ID与当前操作的记录ID不同，说明存在重复的字典类型
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue())
        {
            // 返回不唯一标识
            return UserConstants.NOT_UNIQUE;
        }
        // 返回唯一标识
        return UserConstants.UNIQUE;
    }
}
