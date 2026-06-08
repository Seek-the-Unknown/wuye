package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsUserOwnerMapper;
import com.ruoyi.property.domain.PmsUserOwner;
import com.ruoyi.property.service.IPmsUserOwnerService;

/**
 * 用户业主关联Service业务层实现类
 * 负责处理系统用户与业主信息之间关联关系的业务逻辑操作
 * 
 * @author ruoyi
 */
@Service
public class PmsUserOwnerServiceImpl implements IPmsUserOwnerService {
    
    /** 用户业主关联Mapper数据访问接口 */
    @Autowired
    private PmsUserOwnerMapper pmsUserOwnerMapper;

    /**
     * 根据用户ID查询关联的业主信息
     * 
     * @param userId 系统用户主键ID
     * @return 用户业主关联信息实体对象
     */
    @Override
    public PmsUserOwner selectByUserId(Long userId) {
        // 调用Mapper接口根据用户主键查询关联信息
        return pmsUserOwnerMapper.selectByUserId(userId);
    }

    /**
     * 查询用户业主关联详细记录
     * 
     * @param id 用户业主关联主键ID
     * @return 用户业主关联信息实体对象
     */
    @Override
    public PmsUserOwner selectPmsUserOwnerById(Long id) {
        // 调用Mapper接口根据主键查询关联记录详情
        return pmsUserOwnerMapper.selectPmsUserOwnerById(id);
    }

    /**
     * 查询符合条件的用户业主关联列表数据
     * 
     * @param query 包含查询条件的用户业主关联实体
     * @return 用户业主关联对象集合
     */
    @Override
    public List<PmsUserOwner> selectPmsUserOwnerList(PmsUserOwner query) {
        // 调用Mapper接口根据条件查询关联列表
        return pmsUserOwnerMapper.selectPmsUserOwnerList(query);
    }

    /**
     * 新增用户业主关联记录
     * 
     * @param pmsUserOwner 待新增的关联信息实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsUserOwner(PmsUserOwner pmsUserOwner) {
        // 自动设置记录的创建时间为当前时间
        pmsUserOwner.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入关联数据操作
        return pmsUserOwnerMapper.insertPmsUserOwner(pmsUserOwner);
    }

    /**
     * 修改用户业主关联记录
     * 
     * @param pmsUserOwner 包含修改信息的关联信息实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsUserOwner(PmsUserOwner pmsUserOwner) {
        // 调用Mapper接口执行更新关联数据操作
        return pmsUserOwnerMapper.updatePmsUserOwner(pmsUserOwner);
    }

    /**
     * 删除单条用户业主关联信息
     * 
     * @param id 待删除的关联记录主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsUserOwnerById(Long id) {
        // 调用Mapper接口根据主键删除关联记录
        return pmsUserOwnerMapper.deletePmsUserOwnerById(id);
    }
}
